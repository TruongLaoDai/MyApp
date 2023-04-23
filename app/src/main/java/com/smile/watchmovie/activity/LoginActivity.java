package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.smile.watchmovie.R;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityLoginBinding;
import com.smile.watchmovie.model.User;
import com.smile.watchmovie.model.UserResponse;
import com.smile.watchmovie.model.UserResponseLogin;

import org.json.JSONException;

import java.util.Collections;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ActivityLoginBinding binding;
    CallbackManager callBackManager;
    private String username, password, full_name, picture;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        username = "";
        password = "";
        full_name = "";
        picture = "";

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        callBackManager = CallbackManager.Factory.create();

        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.loginWithGoogle.setOnClickListener(v -> signIn());

        LoginManager.getInstance().registerCallback(callBackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken;
                        accessToken = AccessToken.getCurrentAccessToken();
                        AccessToken finalAccessToken = accessToken;
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                (object, response) -> {
                                    // Application code
                                    try {
                                        assert object != null;
                                        username = (String) object.get("name");
                                        assert finalAccessToken != null;
                                        password = finalAccessToken.getUserId();
                                        full_name = username;
                                        binding.loadingLogin.setVisibility(View.VISIBLE);
                                        picture = object.getJSONObject("picture")
                                                .getJSONObject("data")
                                                .getString("url");
                                        callApiLoginUser("facebook");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link, picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        // App code
                    }
                });

        binding.loginWithFace.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Collections.singletonList("public_profile")));
    }

    public void callApiRegisterUser(String type) {
        ApiService.apiUser.registerUser(username, password, full_name).enqueue(new Callback<UserResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (binding.loadingLogin.getVisibility() == View.VISIBLE) {
                    binding.loadingLogin.setVisibility(View.INVISIBLE);
                }
                if (binding.loadingLogin.getVisibility() == View.VISIBLE) {
                    binding.loadingLogin.setVisibility(View.INVISIBLE);
                }
                if (userResponse != null) {
                    if (userResponse.getMessage().equals("Success")) {
                        navigateToMainActivity(type);
                        saveToSharedPreferences(userResponse.getId() + "", "0", full_name, picture);
                        Toast.makeText(LoginActivity.this, "Login success1", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error Register1", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error Register2", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                binding.loadingLogin.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Error Register3", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callApiLoginUser(String type) {
        ApiService.apiUser.loginUser(username, password).enqueue(new Callback<UserResponseLogin>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<UserResponseLogin> call, @NonNull Response<UserResponseLogin> response) {
                UserResponseLogin userResponse = response.body();
                binding.loadingLogin.setVisibility(View.INVISIBLE);
                if (userResponse != null) {
                    User user = userResponse.getData();
                    saveToSharedPreferences(user.getId_user(), user.isIs_vip(), user.getFull_name() == null ? full_name: user.getFull_name(), picture);
                    navigateToMainActivity(type);
                } else {
                    callApiRegisterUser(type);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponseLogin> call, @NonNull Throwable t) {
                binding.loadingLogin.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToSharedPreferences(String idUser, String isVip, String name, String picture) {
        editor.putString("idUser", idUser);
        editor.putString("isVip", isVip);
        editor.putString("name", name);
        editor.putString("picture", picture);
        editor.apply();
    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                if (acct != null) {
                    username = acct.getEmail();
                    password = acct.getId();
                    full_name = acct.getDisplayName();
                    binding.loadingLogin.setVisibility(View.VISIBLE);
                    callApiLoginUser("google");
                }
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            callBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void navigateToMainActivity(String type) {
        finish();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();
        editor.putString("typeLogin", type);
        editor.apply();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}