package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityPrivateSettingBinding;
import com.smile.watchmovie.databinding.ActivitySettingPlayFilmBinding;

public class SettingPlayFilmActivity extends AppCompatActivity {

    private ActivitySettingPlayFilmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_play_film);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        binding = ActivitySettingPlayFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.titleAutoFullScreen.setOnClickListener(v -> {
            binding.swSettingPlayFullScreen.setChecked(!binding.swSettingPlayFullScreen.isChecked());
            editor.putBoolean("full_screen", binding.swSettingPlayFullScreen.isChecked());
            editor.apply();
        });

        binding.titleAutoPlay.setOnClickListener(v -> {
            binding.swSettingAutoPlay.setChecked(!binding.swSettingAutoPlay.isChecked());
            editor.putBoolean("auto_play", binding.swSettingAutoPlay.isChecked());
            editor.apply();
        });
    }
}