package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.MainActivity;
import com.smile.watchmovie.activity.ShowMoreCategoryFilmActivity;
import com.smile.watchmovie.adapter.FilmAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.adapter.HomeViewPagerAdapter;
import com.smile.watchmovie.custom.HorizontalFlipTransformation;
import com.smile.watchmovie.databinding.FragmentHomeBinding;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    HomeViewPagerAdapter adapter;
    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        adapter = new HomeViewPagerAdapter(getChildFragmentManager(), 2);
        binding.viewPager.setPageTransformer(true, new HorizontalFlipTransformation());
        binding.viewPager.setAdapter(adapter);
        binding.tab.setTabTextColors(Color.parseColor("#777776"), Color.parseColor("#000000"));
        binding.tab.setupWithViewPager(binding.viewPager);
        return binding.getRoot();
    }
}