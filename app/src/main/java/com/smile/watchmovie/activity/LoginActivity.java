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
import com.smile.watchmovie.eventbus.EventNotifyLogIn;
import com.smile.watchmovie.model.UserInfo;
import com.smile.watchmovie.utils.Constant;

import org.greenrobot.eventbus.EventBus;

/* Link hướng dẫn đăng nhập Google: https://developers.google.com/identity/sign-in/android/sign-in?hl=vi */
public class LoginActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ActivityLoginBinding binding;
    private String userId, full_name;
    private SharedPreferences.Editor editor;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.NAME_DATABASE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /* Khởi tạo FireStore Database  */
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(getString(R.string.name_database_firebase));

        /* Khởi tạo đăng nhập bằng tài khoản Google */
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        binding.ivBack.setOnClickListener(v -> finish());

        binding.loginWithGoogle.setOnClickListener(v -> signIn());

        binding.loginWithFace.setOnClickListener(v -> Toast.makeText(this, R.string.feature_deploying, Toast.LENGTH_SHORT).show());
    }

    private void addAccountOfUserToFireStore() {
        collectionReference.document(getString(R.string.table_user)).collection("user" + userId)
                .whereEqualTo("id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

                            /* Lấy thông tin truy vấn */
                            UserInfo userInfo = querySnapshot.getDocuments().get(0).toObject(UserInfo.class);

                            if (userInfo != null) {
                                /* Lưu thông tin người dùng đã tồn tại trên FireBase vào DB */
                                saveToSharedPreferences(userInfo);

                                notifyLogIn();
                            }
                        } else {
                            UserInfo userInfo = new UserInfo(userId, full_name, "", "", "", "0");
                            collectionReference.document(getString(R.string.table_user))
                                    .collection("user" + userId)
                                    .add(userInfo)
                                    .addOnSuccessListener(documentReference -> {
                                        /* Lưu thông tin người dùng lại DB */
                                        saveToSharedPreferences(userInfo);

                                        notifyLogIn();
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Thêm người dùng thất bại"));
                        }
                    } else {
                        Log.w("FireStore", "Truy vấn thông tin không thành công");
                    }
                });
    }

    private void saveToSharedPreferences(UserInfo user) {
        editor.putString(Constant.ID_USER, user.getId());
        editor.putString(Constant.NAME_USER, user.getFullName());
        editor.putString(Constant.IS_VIP, user.isVip());
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
                    userId = acct.getId();
                    full_name = acct.getDisplayName();
                    addAccountOfUserToFireStore();
                }
            } catch (ApiException e) {
                Toast.makeText(this, getString(R.string.notify_login_google_fail), Toast.LENGTH_SHORT).show();
                Log.e("TAG", "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }

    private void notifyLogIn() {
        EventBus.getDefault().post(new EventNotifyLogIn(true));
        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}