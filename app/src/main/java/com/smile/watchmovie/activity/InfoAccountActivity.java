package com.smile.watchmovie.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.EventBus.EventNotifyLogout;
import com.smile.watchmovie.databinding.ActivityInfoAccountBinding;
import com.smile.watchmovie.model.User;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InfoAccountActivity extends AppCompatActivity {
    private ActivityInfoAccountBinding binding;
    private String id_user, documentId;
    private SharedPreferences.Editor editor;
    private GoogleSignInClient gsc;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpFireBase();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        id_user = sharedPreferences.getString("idUser", "");
        documentId = sharedPreferences.getString("documentId", "");
        String type_login = sharedPreferences.getString("typeLogin", "");

        callApiGetUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            Glide.with(this).load(signInAccount.getPhotoUrl()).into(binding.ivAccount);
        }

        binding.btnUpdate.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.edtName.getText()).toString().equals("")) {
                Toast.makeText(this, "Tên người dùng không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                callApiUpdateUser();
            }
        });

        binding.btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(InfoAccountActivity.this)
                        .setTitle("Xác nhận đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi thiết bị này hay không?")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            editor.clear();
                            editor.apply();
                            if (type_login.equals("google")) {
                                logOutWithGoogle();
                            }
                        }).setNegativeButton("Hủy", null)
                        .show());

        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }

    private void setUpFireBase() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("WatchFilm");
    }

    private void callApiGetUser() {
        collectionReference.document("tbluser").collection("user" + id_user).whereEqualTo("id", id_user)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            binding.edtName.setText(user.getFull_name());
                            binding.edtAddress.setText(user.getAddress());
                            binding.edtNumberPhone.setText(user.getPhone());
                            if (user.getGender().equals("1")) {
                                binding.checkBoxMale.setChecked(true);
                            } else if (user.getGender().equals("2")) {
                                binding.checkBoxFemale.setChecked(true);
                            }
                        }
                    } else {
                        Toast.makeText(InfoAccountActivity.this, "Lấy thông tin cá nhân không thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(InfoAccountActivity.this, "Lấy thông tin cá nhân không thành công", Toast.LENGTH_SHORT).show()
                );
    }

    private void callApiUpdateUser() {
        String addressUpdate, phoneNumberUpdate, gender;
        if (binding.edtAddress.getText() == null && binding.edtAddress.getText().toString().trim().equals("")) {
            addressUpdate = "";
        } else {
            addressUpdate = binding.edtAddress.getText().toString().trim();
        }
        if (binding.edtNumberPhone.getText() == null && binding.edtNumberPhone.getText().toString().trim().equals("")) {
            phoneNumberUpdate = "";
        } else {
            phoneNumberUpdate = binding.edtNumberPhone.getText().toString().trim();
        }
        if (binding.checkBoxFemale.isChecked()) {
            gender = "2";
        } else if (binding.checkBoxMale.isChecked()) {
            gender = "1";
        } else {
            gender = "0";
        }

        Map<String, Object> userInfoUpdate = new HashMap<>();
        userInfoUpdate.put("full_name", Objects.requireNonNull(binding.edtName.getText()).toString());
        userInfoUpdate.put("phone", phoneNumberUpdate);
        userInfoUpdate.put("address", addressUpdate);
        userInfoUpdate.put("gender", gender);

        binding.loadUpdateUser.setVisibility(View.VISIBLE);

        collectionReference.document("tbluser")
                .collection("user" + id_user)
                .document(documentId)
                .update(userInfoUpdate)
                .addOnCompleteListener(task -> {
                    binding.loadUpdateUser.setVisibility(View.GONE);
                    Toast.makeText(this, "Cập nhập thông tin thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.loadUpdateUser.setVisibility(View.GONE);
                    Toast.makeText(this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void logOutWithGoogle() {
        gsc.signOut().addOnCompleteListener(task -> {
            EventBus.getDefault().post(new EventNotifyLogout(true));
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}