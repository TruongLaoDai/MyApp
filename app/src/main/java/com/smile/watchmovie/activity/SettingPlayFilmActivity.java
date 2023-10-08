package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.smile.watchmovie.databinding.ActivitySettingPlayFilmBinding;

public class SettingPlayFilmActivity extends AppCompatActivity {
    private ActivitySettingPlayFilmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingPlayFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean full_screen = sharedPreferences.getBoolean("full_screen", false);
        boolean auto_play = sharedPreferences.getBoolean("auto_play", true);

        binding.swSettingPlayFullScreen.setChecked(full_screen);
        binding.swSettingAutoPlay.setChecked(auto_play);

        binding.swSettingPlayFullScreen.setOnClickListener(v -> {
            editor.putBoolean("full_screen", binding.swSettingPlayFullScreen.isChecked());
            editor.apply();
        });

        binding.swSettingAutoPlay.setOnClickListener(v -> {
            editor.putBoolean("auto_play", binding.swSettingAutoPlay.isChecked());
            editor.apply();
        });

        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }
}