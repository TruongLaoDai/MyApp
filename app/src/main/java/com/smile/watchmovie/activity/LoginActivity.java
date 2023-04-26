package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityLoginBinding;
import com.smile.watchmovie.model.User;

import org.json.JSONException;

import java.util.Collections;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ActivityLoginBinding binding;
    CallbackManager callBackManager;
    private String username, password, full_name, picture;
    private SharedPreferences.Editor editor;
    private CollectionReference collectionReference;

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

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("WatchFilm");

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
        User user = new User(password, full_name, "", "", "", "0");
        collectionReference.document("tbluser").collection("user"+password)
                .add(user)
                .addOnCompleteListener(task -> {
                    user.setDocumentId(task.getResult().getId());
                    saveToSharedPreferences(user);
                    navigateToMainActivity(type);
                    Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    public void callApiLoginUser(String type) {
        collectionReference.document("tbluser").collection("user"+password).whereEqualTo("id", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.loadingLogin.setVisibility(View.GONE);
                    if(queryDocumentSnapshots.getDocuments().size() > 0) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        User user = doc.toObject(User.class);
                        if(user != null) {
                            user.setIs_vip("0");
                            user.setDocumentId(doc.getId());
                            saveToSharedPreferences(user);
                            navigateToMainActivity(type);
                            Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            callApiRegisterUser(type);
                        }
                    } else {
                        callApiRegisterUser(type);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.loadingLogin.setVisibility(View.GONE);
                    Toast.makeText(this, "Login fail", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToSharedPreferences(User user) {
        editor.putString("idUser", user.getId());
        editor.putString("documentId", user.getDocumentId());
        editor.putString("isVip", user.isIs_vip());
        editor.putString("name", user.getFull_name());
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