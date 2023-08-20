package com.smile.watchmovie.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.ViewPagerAdapter;
import com.smile.watchmovie.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    public String nameOfUser;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        nameOfUser = sharedPreferences.getString("name", "");

        /* Hiển thị màn hình khởi động khi vào app */
        showSplashHome();

        /* Kết hợp ViewPager với BottomNavigation */
        combineViewPagerWithBottomNavigation();
    }

    private void showSplashHome() {
        new Handler().postDelayed(() -> {
            binding.splashLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
            binding.splashLayout.setVisibility(View.GONE);
            if (nameOfUser != null && !nameOfUser.equals("")) {
                Toast.makeText(MainActivity.this, "Xin chào " + nameOfUser, Toast.LENGTH_SHORT).show();
            }
        }, 2500);
    }

    @SuppressLint("NonConstantResourceId")
    private void combineViewPagerWithBottomNavigation() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.setUserInputEnabled(false);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        binding.bottomNav.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        binding.bottomNav.getMenu().findItem(R.id.action_search).setChecked(true);
                        break;
                    case 2:
                        binding.bottomNav.getMenu().findItem(R.id.action_person).setChecked(true);
                        break;
                }
            }
        });

        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    binding.viewPager.setCurrentItem(0);
                    break;
                case R.id.action_search:
                    binding.viewPager.setCurrentItem(1);
                    break;
                case R.id.action_person:
                    binding.viewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });
    }
}