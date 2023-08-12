package com.smile.watchmovie.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityLoginBinding;
import com.smile.watchmovie.model.User;

public class LoginActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ActivityLoginBinding binding;
    private String username, userId, full_name;
    private SharedPreferences.Editor editor;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /* Khởi tạo FireStore Database  */
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("WatchFilm");

        /* Khởi tạo đăng nhập bằng tài khoản Google */
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        binding.ivBack.setOnClickListener(v -> finish());

        binding.loginWithGoogle.setOnClickListener(v -> signIn());

        binding.loginWithFace.setOnClickListener(v -> Toast.makeText(this, R.string.feature_deploying, Toast.LENGTH_SHORT).show());
    }

    public void addAccountOfUserToFireStore(String type) {
        collectionReference.document("tbluser").collection("user" + userId)
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            /* Lấy thông tin truy vấn */
                            String documentId = querySnapshot.getDocuments().get(0).getId();
                            User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                            if (user != null) {
                                user.setDocumentId(documentId);
                            }

                            /* Lưu thông tin người dùng lại DB */
                            if (user != null) {
                                saveToSharedPreferences(user);
                            }

                            /* Khởi động lại app */
                            navigateToMainActivity(type);

                            Log.e("FireStore", "Thông tin người dùng đã tồn tại");
                        } else {
                            User user = new User(userId, username, full_name, "", "", "", "0");
                            collectionReference.document("tbluser")
                                    .collection("user" + userId)
                                    .add(user)
                                    .addOnSuccessListener(documentReference -> {
                                        String documentId = documentReference.getId();
                                        user.setDocumentId(documentId);

                                        /* Lưu thông tin người dùng lại DB */
                                        saveToSharedPreferences(user);

                                        /* Khởi động lại app */
                                        navigateToMainActivity(type);

                                        Log.d("Firestore", "Thêm thông tin người dùng thành công");
                                    })
                                    .addOnFailureListener(e -> Log.w("Firestore", "Thêm thông tin người dùng thất bại"));
                        }
                    } else {
                        Log.w("FireStore", "Truy vấn thông tin không thành công");
                    }
                });
    }

    private void saveToSharedPreferences(User user) {
        editor.putString("idUser", user.getId());
        editor.putString("isVip", user.isIs_vip());
        editor.putString("name", user.getFull_name());
        editor.putString("documentId", user.getDocumentId());
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
                    userId = acct.getId();
                    full_name = acct.getDisplayName();
                    addAccountOfUserToFireStore("google");
                }
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToMainActivity(String type) {
        editor.putString("typeLogin", type);
        editor.apply();
        Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}