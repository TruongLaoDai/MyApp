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

import com.smile.watchmovie.activity.MainActivity;
import com.smile.watchmovie.activity.ShowMoreCategoryFilmActivity;
import com.smile.watchmovie.adapter.FilmAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentHomeBinding;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FilmAdapter mFilmAdapter1;
    private FilmAdapter mFilmAdapter2;
    private FilmAdapter mFilmAdapter3;
    private FilmAdapter mFilmAdapter4;
    private FilmAdapter mFilmAdapter5;
    private FilmAdapter mFilmAdapter6;
    private FilmAdapter mFilmAdapter7;
    private MainActivity mMainActivity;
    private int callAgaint = 0;
    private List<FilmMainHome> movieMainHomeList;
    private List<FilmMainHome> movieCategory13;
    private List<FilmMainHome> movieCategory6;
    private List<FilmMainHome> movieCategory4;
    private List<FilmMainHome> movieCategory11;
    private List<FilmMainHome> movieCategory12;
    private List<FilmMainHome> movieCategory14;
    private List<FilmMainHome> movieCategory15;

    public HomeFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mFilmAdapter1 = new FilmAdapter(mMainActivity);
        mFilmAdapter2 = new FilmAdapter(mMainActivity);
        mFilmAdapter3 = new FilmAdapter(mMainActivity);
        mFilmAdapter4 = new FilmAdapter(mMainActivity);
        mFilmAdapter5 = new FilmAdapter(mMainActivity);
        mFilmAdapter6 = new FilmAdapter(mMainActivity);
        mFilmAdapter7 = new FilmAdapter(mMainActivity);

        movieMainHomeList = new ArrayList<>();
        movieCategory13 = new ArrayList<>();
        movieCategory6 = new ArrayList<>();
        movieCategory4 = new ArrayList<>();
        movieCategory11 = new ArrayList<>();
        movieCategory12 = new ArrayList<>();
        movieCategory14 = new ArrayList<>();
        movieCategory15 = new ArrayList<>();

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvFilm1.setLayoutManager(linearLayoutManager1);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvFilm2.setLayoutManager(linearLayoutManager2);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvFilm3.setLayoutManager(linearLayoutManager3);
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvFilm4.setLayoutManager(linearLayoutManager4);
        LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvFilm5.setLayoutManager(linearLayoutManager5);
        LinearLayoutManager linearLayoutManager6 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvFilm6.setLayoutManager(linearLayoutManager6);
        LinearLayoutManager linearLayoutManager7 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvFilm7.setLayoutManager(linearLayoutManager7);

        callApiGetHomeFilmByCategory(15, 0);
        binding.moreCartonMovie.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 14);
            startActivity(intent);
        });
        binding.moreActionMovie.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 6);
            startActivity(intent);
        });
        binding.moreHorrorMovie.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 4);
            startActivity(intent);
        });
        binding.moreThrillerMovie.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 11);
            startActivity(intent);
        });
        binding.moreComedyMovie.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 12);
            startActivity(intent);
        });
        binding.moreRomanticMovie.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 13);
            startActivity(intent);
        });
        binding.moreAdventureMovie.setOnClickListener(v -> {
            Intent intent = new Intent(mMainActivity, ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 15);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    public void callApiGetHomeFilmByCategory(int categoryId, int page) {
        ApiService.apiService.getFilmByCategory("7da353b8a3246f851e0ee436d898a26d", categoryId, page, 5).enqueue(new Callback<FilmArrayResponse>() {

            @Override
            public void onResponse(@NonNull Call<FilmArrayResponse> call, @NonNull Response<FilmArrayResponse> response) {
                FilmArrayResponse movieArrayResponse = response.body();
                if (movieArrayResponse != null) {
                    switch (categoryId){
                        case 14:
                            movieCategory14.addAll(movieArrayResponse.getData());
                            mFilmAdapter1.setData(movieArrayResponse.getData());
                            binding.rcvFilm1.setAdapter(mFilmAdapter1);
                            callAgaint += 1;
                            callApiGetHomeFilmByCategory(15, 0);
                            break;
                        case 6:
                            movieCategory6.addAll(movieArrayResponse.getData());
                            mFilmAdapter2.setData(movieArrayResponse.getData());
                            binding.rcvFilm2.setAdapter(mFilmAdapter2);
                            callApiGetHomeFilmByCategory(14, 0);
                            break;
                        case 4:
                            movieCategory4.addAll(movieArrayResponse.getData());
                            mFilmAdapter3.setData(movieArrayResponse.getData());
                            binding.rcvFilm3.setAdapter(mFilmAdapter3);
                            callApiGetHomeFilmByCategory(6, 0);
                            break;
                        case 11:
                            movieCategory11.addAll(movieArrayResponse.getData());
                            mFilmAdapter4.setData(movieArrayResponse.getData());
                            binding.rcvFilm4.setAdapter(mFilmAdapter4);
                            callApiGetHomeFilmByCategory(4, 0);
                            break;
                        case 12:
                            movieCategory12.addAll(movieArrayResponse.getData());
                            mFilmAdapter5.setData(movieArrayResponse.getData());
                            binding.rcvFilm5.setAdapter(mFilmAdapter5);
                            callApiGetHomeFilmByCategory(11, 0);
                            break;
                        case 13:
                            movieCategory13.addAll(movieArrayResponse.getData());
                            mFilmAdapter6.setData(movieArrayResponse.getData());
                            binding.rcvFilm6.setAdapter(mFilmAdapter6);
                            callApiGetHomeFilmByCategory(12, 0);
                            break;
                        case 15:
                            if(callAgaint < 2) {
                                movieCategory15.addAll(movieArrayResponse.getData());
                                mFilmAdapter7.setData(movieArrayResponse.getData());
                                binding.rcvFilm7.setAdapter(mFilmAdapter7);
                                callApiGetHomeFilmByCategory(13, 0);
                                break;
                            }else{
                                binding.loadHomePage.setVisibility(View.INVISIBLE);
                                break;
                            }
                    }
                }
                else{
                    Toast.makeText(mMainActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
                //Toast.makeText(mMainActivity, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
