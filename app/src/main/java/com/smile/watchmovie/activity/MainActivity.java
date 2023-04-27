package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.ViewPagerAdapter;
import com.smile.watchmovie.custom.CustomViewPager;
import com.smile.watchmovie.databinding.ActivityMainBinding;
import com.smile.watchmovie.notification.NotificationReceiver;

import org.json.JSONException;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    public ActivityMainBinding binding;
    private BottomNavigationView mNavigationView;
    private CustomViewPager mViewPager;
    private String mTypeLogin;
    public ImageView ivLogoApp;
    public String nameUser;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mNavigationView = binding.bottomNav;
        mViewPager = binding.viewPager;
        ImageView ivLoginLogout = binding.iconLoginLogout;
        ivLogoApp = binding.ivLogoApp;
        mTypeLogin = "";
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        createNotificationChannel();
        showSplashHome();
        mTypeLogin = sharedPreferences.getString("typeLogin", "");

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct != null) {
            mTypeLogin = "google";
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            mTypeLogin = "facebook";
        }
        mTypeLogin = "google";

        if (mTypeLogin.equals("google")) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso);

            if (acct != null) {
                nameUser = acct.getDisplayName();
            }
        } else {
            if (mTypeLogin.equals("facebook")) {
                accessToken = AccessToken.getCurrentAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        (object, response) -> {
                            // Application code
                            try {
                                assert object != null;
                                nameUser = (String) object.get("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
            } else {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            }
        }

        if (mTypeLogin.equals("")) {
            ivLoginLogout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        } else {
            ivLoginLogout.setOnClickListener(v -> {
                editor.clear();
                editor.apply();
                if (mTypeLogin.equals("google")) {
                    logOutWithGoogle();
                } else {
                    if (mTypeLogin.equals("facebook")) {
                        logOutWithFaceBook();
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }
            });
        }

        setUpViewPager();
        mNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.action_search:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.action_person:
                    mViewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });
    }

    private void showSplashHome() {
        new Handler().postDelayed(() -> {
            if (binding.splashLayout.getVisibility() == View.VISIBLE) {
                binding.splashLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                binding.splashLayout.setVisibility(View.GONE);
                setAlarmManager();
                if (nameUser != null)
                    Toast.makeText(MainActivity.this, "Xin chào " + nameUser, Toast.LENGTH_SHORT).show();
            }
        }, 2500);
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setPageScrollEnabled(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        mNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        break;
                    case 2:
                        mNavigationView.getMenu().findItem(R.id.action_person).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void logOutWithGoogle() {
        gsc.signOut().addOnCompleteListener(task -> {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    void logOutWithFaceBook() {
        LoginManager.getInstance().logOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "watchMediaWeatherChannel";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("watchmedia", name, importance);

            NotificationManagerCompat.from(this).createNotificationChannel(channel);

        }
    }

    private void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}