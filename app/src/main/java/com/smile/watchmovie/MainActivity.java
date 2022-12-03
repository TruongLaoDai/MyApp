package com.smile.watchmovie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.smile.watchmovie.adapter.ViewPagerAdapter;
import com.smile.watchmovie.databinding.ActivityMainBinding;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    public ActivityMainBinding binding;
    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private TextView tvUser;
    private String mTypeLogin;
    public ImageView ivLogoApp;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mNavigationView = binding.bottomNav;
        mViewPager = binding.viewPager;
        tvUser = binding.tvUser;
        ImageView ivLoginLogout = binding.iconLoginLogout;
        ivLogoApp = binding.ivLogoApp;

        mTypeLogin = getIntent().getStringExtra("type");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if(acct != null){
            mTypeLogin = "google";
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken!=null&& !accessToken.isExpired()){
            mTypeLogin = "facebook";
        }
        if(mTypeLogin==null){
            Toast.makeText(this, "You need login!!", Toast.LENGTH_SHORT).show();
        }else{
            if(mTypeLogin.equals("google")) {
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                gsc = GoogleSignIn.getClient(this, gso);

                if(acct!=null) {
                    tvUser.setText(acct.getDisplayName());
                    Toast.makeText(this, "Wellcome " + acct.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
            }else {
                if (mTypeLogin.equals("facebook")) {
                    accessToken = AccessToken.getCurrentAccessToken();
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            (object, response) -> {
                                // Application code
                                try {
                                    assert object != null;
                                    String fullname = (String) object.get("name");
                                    tvUser.setText(fullname);
                                    Toast.makeText(MainActivity.this, "Wellcome " + fullname, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link");
                    request.setParameters(parameters);
                    request.executeAsync();
                }else{
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(mTypeLogin==null){
            ivLoginLogout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        }
        else {
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
            switch (item.getItemId()){
                case R.id.action_home:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.action_search:
                    mViewPager.setCurrentItem(1);
                    break;
            }
            return true;
        });
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.toolBarHome.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams params1=(LinearLayout.LayoutParams) binding.toolBarHome.getLayoutParams();
                        params1.width= ViewGroup.LayoutParams.MATCH_PARENT;
                        params1.height= (int)(65*getApplicationContext().getResources().getDisplayMetrics().density);
                        binding.toolBarHome.setLayoutParams(params1);
                        mNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        binding.toolBarHome.setVisibility(View.GONE);
                        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams) binding.viewPager.getLayoutParams();
                        params.width= ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height= ViewGroup.LayoutParams.MATCH_PARENT;
                        binding.viewPager.setLayoutParams(params);
                        mNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void logOutWithGoogle(){
        gsc.signOut().addOnCompleteListener(task -> {
            finish();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        });
    }
    void logOutWithFaceBook(){
        LoginManager.getInstance().logOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }
}