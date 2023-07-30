package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.smile.watchmovie.EventBus.EventNotifyLogout;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ActivityPrivateSettingBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PrivateSettingActivity extends AppCompatActivity {
    private ActivityPrivateSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_setting);

        binding = ActivityPrivateSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.settingPlayFilm.setOnClickListener(v ->
                startActivity(new Intent(PrivateSettingActivity.this, SettingPlayFilmActivity.class))
        );

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString("idUser", "");

        if (idUser.equals("")) {
            binding.settingAccount.setVisibility(View.GONE);
        }

        binding.settingAccount.setOnClickListener(v ->
                startActivity(new Intent(PrivateSettingActivity.this, InfoAccountActivity.class))
        );

        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventNotifyLogout isLogout) {
        if (isLogout.isLogout()) {
            binding.settingAccount.setVisibility(View.GONE);
        }
    }
}