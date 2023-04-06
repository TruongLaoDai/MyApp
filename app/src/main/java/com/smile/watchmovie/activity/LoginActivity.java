package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.User;
import com.smile.watchmovie.model.UserResponse;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ActivityLoginBinding binding;
    CallbackManager callBackManager;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        username = "";
        password = "";

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        callBackManager = CallbackManager.Factory.create();

        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.loginWithGoogle.setOnClickListener(v -> signIn());

        LoginManager.getInstance().registerCallback(callBackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        navigateToMainActivity("facebook");
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

    public void callApiRegisterUser() {
        ApiService.apiUser.registerUser(username, password).enqueue(new Callback<UserResponse>() {
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
                    if(userResponse.getMessage().equals("success")){
                        navigateToMainActivity("google");
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

    public void callApiLoginUser() {
        ApiService.apiUser.loginUser(username, password).enqueue(new Callback<User>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User user = response.body();
                binding.loadingLogin.setVisibility(View.INVISIBLE);
                if (user != null) {
                    navigateToMainActivity("google");
                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                } else {
                    callApiRegisterUser();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                binding.loadingLogin.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
            }
        });
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
                if(acct != null){
                    username = acct.getEmail();
                    password = acct.getId();
                    callApiLoginUser();
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
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}