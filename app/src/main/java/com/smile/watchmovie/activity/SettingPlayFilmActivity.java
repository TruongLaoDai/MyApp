package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivitySettingPlayFilmBinding;
import com.smile.watchmovie.utils.Constant;

public class SettingPlayFilmActivity extends AppCompatActivity {
    private ActivitySettingPlayFilmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingPlayFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.name_database_sharedPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean full_screen = sharedPreferences.getBoolean(Constant.FULL_SCREEN, false);
        boolean auto_play = sharedPreferences.getBoolean(Constant.AUTO_PLAY, true);

        binding.swSettingPlayFullScreen.setChecked(full_screen);
        binding.swSettingAutoPlay.setChecked(auto_play);

        binding.swSettingPlayFullScreen.setOnClickListener(v -> {
            editor.putBoolean(Constant.FULL_SCREEN, binding.swSettingPlayFullScreen.isChecked());
            editor.apply();
        });

        binding.swSettingAutoPlay.setOnClickListener(v -> {
            editor.putBoolean(Constant.AUTO_PLAY, binding.swSettingAutoPlay.isChecked());
            editor.apply();
        });

        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }
}