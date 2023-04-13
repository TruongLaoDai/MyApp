package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityChoosePaymentBinding;
import com.smile.watchmovie.databinding.ActivityPrivateSettingBinding;

public class PrivateSettingActivity extends AppCompatActivity {

    private ActivityPrivateSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_setting);

        binding = ActivityPrivateSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.settingPlayFilm.setOnClickListener(v -> {
            Intent intent = new Intent(PrivateSettingActivity.this, SettingPlayFilmActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString("idUser", "");

        if(idUser.equals("")) {
            binding.settingAccount.setVisibility(View.GONE);
        }
        binding.settingAccount.setOnClickListener(v -> {
            Intent intent = new Intent(PrivateSettingActivity.this, InfoAccountActivity.class);
            startActivity(intent);
        });
    }
}