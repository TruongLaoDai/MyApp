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
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityInfoAccountBinding;
import com.smile.watchmovie.eventbus.EventNotifyLogIn;
import com.smile.watchmovie.model.UserInfo;

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

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.name_database_sharedPreferences), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        id_user = sharedPreferences.getString(getString(R.string.id_user), "");
        documentId = sharedPreferences.getString(getString(R.string.document_id), "");

        /* Gọi lấy thông tin user trên FireBase */
        callApiGetUser();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            Glide.with(this).load(signInAccount.getPhotoUrl()).into(binding.ivAccount);
        }

        handleEventClick();
    }

    private void handleEventClick() {
        binding.toolBar.setNavigationOnClickListener(view -> finish());

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
                            logOutWithGoogle();
                        }).setNegativeButton("Hủy", null)
                        .show());
    }

    private void setUpFireBase() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(getString(R.string.name_database_firebase));
    }

    private void callApiGetUser() {
        collectionReference.document(getString(R.string.table_user)).collection("user" + id_user).whereEqualTo("id", id_user)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        UserInfo user = queryDocumentSnapshots.getDocuments().get(0).toObject(UserInfo.class);
                        if (user != null) {
                            binding.edtName.setText(user.getFullName());
                            binding.edtAddress.setText(user.getAddress());
                            binding.edtNumberPhone.setText(user.getPhone());
                            if (user.getGender().equals("1")) {
                                binding.checkBoxMale.setChecked(true);
                            } else if (user.getGender().equals("2")) {
                                binding.checkBoxFemale.setChecked(true);
                            }
                        }
                    } else {
                        Toast.makeText(InfoAccountActivity.this, "Lấy thông tin cá nhân thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lấy thông tin cá nhân thất bại", Toast.LENGTH_SHORT).show()
                );
    }

    private void callApiUpdateUser() {
        String address, phoneNumber, gender;
        address = Objects.requireNonNull(binding.edtAddress.getText()).toString().trim();
        phoneNumber = Objects.requireNonNull(binding.edtNumberPhone.getText()).toString().trim();
        if (binding.checkBoxFemale.isChecked()) {
            gender = "2";
        } else if (binding.checkBoxMale.isChecked()) {
            gender = "1";
        } else {
            gender = "0";
        }

        Map<String, Object> userInfoUpdate = new HashMap<>();
        userInfoUpdate.put("fullName", Objects.requireNonNull(binding.edtName.getText()).toString());
        userInfoUpdate.put("phone", phoneNumber);
        userInfoUpdate.put("address", address);
        userInfoUpdate.put("gender", gender);

        binding.clLoading.setVisibility(View.VISIBLE);

        collectionReference.document(getString(R.string.table_user))
                .collection("user" + id_user)
                .document(documentId)
                .update(userInfoUpdate)
                .addOnCompleteListener(task -> {
                    binding.clLoading.setVisibility(View.GONE);
                    Toast.makeText(this, "Cập nhập thông tin thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.clLoading.setVisibility(View.GONE);
                    Toast.makeText(this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void logOutWithGoogle() {
        gsc.signOut().addOnCompleteListener(task -> {
            EventBus.getDefault().post(new EventNotifyLogIn(false));
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}