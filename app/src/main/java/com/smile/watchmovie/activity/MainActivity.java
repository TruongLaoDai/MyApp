package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONException;

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
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        showSplashHome();

        mTypeLogin = getIntent().getStringExtra("type");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct != null) {
            mTypeLogin = "google";
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            mTypeLogin = "facebook";
        }

        if (mTypeLogin.equals("google")) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso);

            if (acct != null) {
                editor.putString("idUser", acct.getId());
                editor.apply();
                nameUser = acct.getDisplayName();
            }
        } else {
            if (mTypeLogin.equals("facebook")) {
                accessToken = AccessToken.getCurrentAccessToken();
                AccessToken finalAccessToken = accessToken;
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        (object, response) -> {
                            // Application code
                            try {
                                assert object != null;
                                nameUser = (String) object.get("name");
                                editor.putString("idUser", finalAccessToken.getUserId());
                                editor.apply();
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

        if (mTypeLogin == null) {
            ivLoginLogout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        } else {
            ivLoginLogout.setOnClickListener(v -> {
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
                        if (binding.toolBarHome.getVisibility() == View.GONE) {
                            binding.toolBarHome.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) binding.toolBarHome.getLayoutParams();
                            params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params1.height = (int) (65 * getApplicationContext().getResources().getDisplayMetrics().density);
                            binding.toolBarHome.setLayoutParams(params1);
                        }
                        mNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        binding.toolBarHome.setVisibility(View.GONE);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.viewPager.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        binding.viewPager.setLayoutParams(params);
                        mNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        break;
                    case 2:
                        binding.toolBarHome.setVisibility(View.GONE);
                        LinearLayout.LayoutParams paramsFragmentPerson = (LinearLayout.LayoutParams) binding.viewPager.getLayoutParams();
                        paramsFragmentPerson.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        paramsFragmentPerson.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        binding.viewPager.setLayoutParams(paramsFragmentPerson);
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
}