package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.MainActivity;
import com.smile.watchmovie.PaginationScrollListener;
import com.smile.watchmovie.adapter.FilmAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentHomeBinding;
import com.smile.watchmovie.model.MovieArrayResponse;
import com.smile.watchmovie.model.MovieMainHome;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FilmAdapter mFilmAdapter;
    private MainActivity mMainActivity;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage=0;
    private final int mTotalPage=20;
    private List<MovieMainHome> movieMainHomeList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainActivity = (MainActivity) getActivity();
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mFilmAdapter = new FilmAdapter(mMainActivity);
        binding.ivMoveToHeadPage.setVisibility(View.INVISIBLE);
        binding.loadMore.setVisibility(View.INVISIBLE);
        movieMainHomeList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mMainActivity, 2);
        binding.rcvFilm.setLayoutManager(gridLayoutManager);
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(mMainActivity, DividerItemDecoration.VERTICAL);
        binding.rcvFilm.addItemDecoration(itemDecoration);
        binding.rcvFilm.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            public void loadMoreItems() {
                mIsLoading=true;
                binding.loadMore.setVisibility(View.VISIBLE);
                mCurrentPage+=1;
                loadNextPage();
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }
        });

        binding.ivMoveToHeadPage.setVisibility(View.VISIBLE);
        binding.ivMoveToHeadPage.setOnClickListener(v -> binding.rcvFilm.setAdapter(mFilmAdapter));

        callApiGetHomeFilm(0);
        return binding.getRoot();
    }

    public void callApiGetHomeFilm(int page) {
        ApiService.apiService.getDataHomeFilms("7da353b8a3246f851e0ee436d898a26d", "094555566", page).enqueue(new Callback<MovieArrayResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieArrayResponse> call, @NonNull Response<MovieArrayResponse> response) {
                MovieArrayResponse movieArrayResponse = response.body();
                if (movieArrayResponse != null) {
                    binding.loadHomePage.setVisibility(View.INVISIBLE);
                    movieMainHomeList.addAll(movieArrayResponse.getData());
                    mFilmAdapter.setData(movieMainHomeList);
                    binding.rcvFilm.setAdapter(mFilmAdapter);
                }
                else{
                    Toast.makeText(mMainActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieArrayResponse> call, @NonNull Throwable t) {
                Toast.makeText(mMainActivity, "Error Get Video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage(){
        Handler handler=new Handler();
        handler.postDelayed(() -> {
            mIsLoading=false;
            callApiGetMoreHomeFilm(mCurrentPage);
            binding.loadHomePage.setVisibility(View.VISIBLE);
            binding.loadMore.setVisibility(View.INVISIBLE);
            if(mCurrentPage==mTotalPage){
                mIsLastPage=true;
            }
        },3000);
    }

    private void callApiGetMoreHomeFilm(int page) {
        ApiService.apiService.getDataHomeFilms("7da353b8a3246f851e0ee436d898a26d", "094555566", page).enqueue(new Callback<MovieArrayResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<MovieArrayResponse> call, @NonNull Response<MovieArrayResponse> response) {
                MovieArrayResponse movieArrayResponse = response.body();
                if (movieArrayResponse != null) {
                    binding.loadHomePage.setVisibility(View.INVISIBLE);
                    movieMainHomeList.addAll(movieArrayResponse.getData());
                    mFilmAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(mMainActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieArrayResponse> call, @NonNull Throwable t) {
                Toast.makeText(mMainActivity, "Error Get Video", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
