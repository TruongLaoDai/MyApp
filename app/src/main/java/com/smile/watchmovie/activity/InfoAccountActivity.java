package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.smile.watchmovie.R;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityInfoAccountBinding;
import com.smile.watchmovie.model.User;
import com.smile.watchmovie.model.UserResponse;
import com.smile.watchmovie.model.UserResponseLogin;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoAccountActivity extends AppCompatActivity {

    private ActivityInfoAccountBinding binding;
    private String id_user;
    private SharedPreferences.Editor editor;
    private GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);

        binding = ActivityInfoAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolBar();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String nameUser = sharedPreferences.getString("name", "");
        id_user = sharedPreferences.getString("idUser", "");
        String type_login = sharedPreferences.getString("typeLogin", "");

        callApiGetUser();

        binding.edtName.setText(nameUser);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        binding.btnUpdate.setOnClickListener(v -> {
            binding.loadUpdateUser.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(binding.edtName.getText()).toString().equals("")) {
                new AlertDialog.Builder(InfoAccountActivity.this)
                        .setTitle("Điền thiếu thông tin")
                        .setMessage("Bạn không được để tên rỗng!")
                        .setPositiveButton("OK", (dialog, which) -> {
                        }).show();
            } else {
                callApiUpdateUser();
            }
        });

        binding.btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(InfoAccountActivity.this)
                        .setTitle("Xác nhận đăng xuất?")
                        .setMessage("Bạn không được để tên rỗng!")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            editor.clear();
                            editor.apply();
                            if (type_login.equals("google")) {
                                logOutWithGoogle();
                            } else {
                                logOutWithFacebook();
                            }
                        }).setNegativeButton("Không", null)
                        .show());

        binding.checkBoxMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.checkBoxFemale.setChecked(false);
            }
        });

        binding.checkBoxFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.checkBoxMale.setChecked(false);
            }
        });
    }

    private void setupToolBar() {
        binding.toolBar.setTitle("Thông tin tài khoản");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.toolBar.setTitleTextColor(getColor(R.color.white));
        }
        binding.toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        binding.toolBar.setNavigationOnClickListener(v -> finish());
    }

    private void callApiGetUser() {
        ApiService.apiUser.getUser(id_user).enqueue(new Callback<UserResponseLogin>() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onResponse(@NonNull Call<UserResponseLogin> call, @NonNull Response<UserResponseLogin> response) {
                UserResponseLogin userResponse = response.body();
                if (userResponse != null) {
                    User user = userResponse.getData();
                    binding.edtName.setText(user.getFull_name());
                    binding.edtAddress.setText(user.getAddress());
                    binding.edtNumberPhone.setText(user.getPhone());
                    if(user.getGender().equals("1")) {
                        binding.checkBoxMale.setChecked(true);
                    } else if(user.getGender().equals("2")) {
                        binding.checkBoxFemale.setChecked(true);
                    }
                } else {
                    Toast.makeText(InfoAccountActivity.this, "Thông tin tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                }
                binding.loadUpdateUser.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<UserResponseLogin> call, @NonNull Throwable t) {
                binding.loadUpdateUser.setVisibility(View.GONE);
                Toast.makeText(InfoAccountActivity.this, "Lấy thông tin cá nhân lỗi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callApiUpdateUser() {
        String addressUpdate, phoneNumberUpdate, gender;
        if(binding.edtAddress.getText() == null && binding.edtAddress.getText().toString().trim().equals("")) {
            addressUpdate = "";
        } else {
            addressUpdate = binding.edtAddress.getText().toString().trim();
        }
        if(binding.edtNumberPhone.getText() == null && binding.edtNumberPhone.getText().toString().trim().equals("")) {
            phoneNumberUpdate = "";
        } else {
            phoneNumberUpdate = binding.edtNumberPhone.getText().toString().trim();
        }
        if(binding.checkBoxFemale.isChecked()) {
            gender = "2";
        } else if(binding.checkBoxMale.isChecked()) {
            gender = "1";
        } else {
            gender = "0";
        }
        ApiService.apiUser.updateUser(id_user, Objects.requireNonNull(binding.edtName.getText()).toString(), addressUpdate, phoneNumberUpdate, gender).enqueue(new Callback<UserResponse>() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    if (userResponse.getMessage().equals("Success")) {
                        editor.putString("name", binding.edtName.getText().toString());
                        editor.apply();
                        new AlertDialog.Builder(InfoAccountActivity.this)
                                .setTitle("Cập nhật thành công")
                                .setMessage("Thông tin cá nhân của bạn đã được cập nhật")
                                .setPositiveButton("OK", (dialog, which) -> {
                                }).show();
                    } else {
                        Toast.makeText(InfoAccountActivity.this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InfoAccountActivity.this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
                }
                binding.loadUpdateUser.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                binding.loadUpdateUser.setVisibility(View.GONE);
                Toast.makeText(InfoAccountActivity.this, "Cập nhật thông tin không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void logOutWithGoogle() {
        gsc.signOut().addOnCompleteListener(task -> {
            finish();
            startActivity(new Intent(InfoAccountActivity.this, LoginActivity.class));
        });
    }

    void logOutWithFacebook() {
        LoginManager.getInstance().logOut();
        startActivity(new Intent(InfoAccountActivity.this, LoginActivity.class));
        finish();
    }
}