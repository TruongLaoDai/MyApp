package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smile.watchmovie.activity.ShowMoreCategoryFilmActivity;
import com.smile.watchmovie.adapter.FilmAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentHomeBinding;
import com.smile.watchmovie.model.FilmArrayResponse;

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

    public HomeFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mFilmAdapter1 = new FilmAdapter(requireActivity());
        mFilmAdapter2 = new FilmAdapter(requireActivity());
        mFilmAdapter3 = new FilmAdapter(requireActivity());
        mFilmAdapter4 = new FilmAdapter(requireActivity());
        mFilmAdapter5 = new FilmAdapter(requireActivity());
        mFilmAdapter6 = new FilmAdapter(requireActivity());
        mFilmAdapter7 = new FilmAdapter(requireActivity());

        callApiGetHomeFilmByCategory(15, 0);

        binding.moreCartonMovie.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 14);
            startActivity(intent);
        });

        binding.moreActionMovie.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 6);
            startActivity(intent);
        });

        binding.moreHorrorMovie.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 4);
            startActivity(intent);
        });

        binding.moreThrillerMovie.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 11);
            startActivity(intent);
        });

        binding.moreComedyMovie.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 12);
            startActivity(intent);
        });

        binding.moreRomanticMovie.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShowMoreCategoryFilmActivity.class);
            intent.putExtra("categoryId", 13);
            startActivity(intent);
        });

        binding.moreAdventureMovie.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShowMoreCategoryFilmActivity.class);
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
                    switch (categoryId) {
                        case 14:
                            mFilmAdapter1.setData(movieArrayResponse.getData());
                            binding.rcvFilm1.setAdapter(mFilmAdapter1);
                            callApiGetHomeFilmByCategory(15, 0);
                            break;
                        case 6:
                            mFilmAdapter2.setData(movieArrayResponse.getData());
                            binding.rcvFilm2.setAdapter(mFilmAdapter2);
                            callApiGetHomeFilmByCategory(14, 0);
                            break;
                        case 4:
                            mFilmAdapter3.setData(movieArrayResponse.getData());
                            binding.rcvFilm3.setAdapter(mFilmAdapter3);
                            callApiGetHomeFilmByCategory(6, 0);
                            break;
                        case 11:
                            mFilmAdapter4.setData(movieArrayResponse.getData());
                            binding.rcvFilm4.setAdapter(mFilmAdapter4);
                            callApiGetHomeFilmByCategory(4, 0);
                            break;
                        case 12:
                            mFilmAdapter5.setData(movieArrayResponse.getData());
                            binding.rcvFilm5.setAdapter(mFilmAdapter5);
                            callApiGetHomeFilmByCategory(11, 0);
                            break;
                        case 13:
                            mFilmAdapter6.setData(movieArrayResponse.getData());
                            binding.rcvFilm6.setAdapter(mFilmAdapter6);
                            callApiGetHomeFilmByCategory(12, 0);
                            break;
                        case 15:
                            mFilmAdapter7.setData(movieArrayResponse.getData());
                            binding.rcvFilm7.setAdapter(mFilmAdapter7);
                            callApiGetHomeFilmByCategory(13, 0);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
            }
        });
    }
}