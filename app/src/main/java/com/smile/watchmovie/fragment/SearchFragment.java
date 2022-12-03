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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.MainActivity;
import com.smile.watchmovie.PaginationScrollListener;
import com.smile.watchmovie.adapter.FilmAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentSearchBinding;
import com.smile.watchmovie.model.MovieArrayResponse;
import com.smile.watchmovie.model.MovieMainHome;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private FilmAdapter mFilmAdapter;
    private MainActivity mMainActivity;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage=0;
    private final int mTotalPage=20;
    private String key;
    private List<MovieMainHome> movieMainHomeList;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        mMainActivity = (MainActivity) getActivity();

        mFilmAdapter = new FilmAdapter(mMainActivity);

        binding.loadMore.setVisibility(View.INVISIBLE);
        binding.loadSearchPage.setVisibility(View.INVISIBLE);
        movieMainHomeList = new ArrayList<>();

        binding.searchView.clearFocus();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.loadSearchPage.setVisibility(View.VISIBLE);
                key = query;
                movieMainHomeList.clear();
                if(query.equals("")){
                    binding.loadSearchPage.setVisibility(View.INVISIBLE);
                    binding.tvTitle.setText("Bạn hãy nhập tên phim cần tìm!");
                    binding.tvTitle.setVisibility(View.VISIBLE);
                    mFilmAdapter.setData(movieMainHomeList);
                    binding.rcvFilm.setAdapter(mFilmAdapter);
                }
                else {
                    binding.tvTitle.setVisibility(View.INVISIBLE);
                    callApiSearchFilm(query, 0);
                }
                return false;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextChange(String newText) {
                binding.loadSearchPage.setVisibility(View.VISIBLE);
                key = newText;
                movieMainHomeList.clear();
                if(newText.equals("")){
                    binding.loadSearchPage.setVisibility(View.INVISIBLE);
                    binding.tvTitle.setText("Bạn hãy nhập tên phim cần tìm!");
                    binding.tvTitle.setVisibility(View.VISIBLE);
                    mFilmAdapter.setData(movieMainHomeList);
                    binding.rcvFilm.setAdapter(mFilmAdapter);
                }
                else {
                    binding.tvTitle.setVisibility(View.INVISIBLE);
                    callApiSearchFilm(newText, 0);
                }
                return false;
            }
        });

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
        return binding.getRoot();
    }

    private void callApiSearchFilm(String key, int page) {
        ApiService.apiService.searchFilms("7da353b8a3246f851e0ee436d898a26d", key, page, 10).enqueue(new Callback<MovieArrayResponse>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<MovieArrayResponse> call, @NonNull Response<MovieArrayResponse> response) {
                MovieArrayResponse movieArrayResponse = response.body();
                if (movieArrayResponse != null) {
                    if(!key.equals("")) {
                        binding.loadSearchPage.setVisibility(View.INVISIBLE);
                        movieMainHomeList.addAll(movieArrayResponse.getData());
                        if (page == 0) {
                            mFilmAdapter.setData(movieMainHomeList);
                            binding.rcvFilm.setAdapter(mFilmAdapter);
                        }
                        mFilmAdapter.notifyDataSetChanged();
                        if (movieMainHomeList.size() > 0 && movieArrayResponse.getData().size() == 0) {
                            Toast.makeText(mMainActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                        } else if (movieMainHomeList.size() == 0) {
                            binding.tvTitle.setVisibility(View.VISIBLE);
                            binding.tvTitle.setText("Không có phim bạn cần tìm!");
                        }
                    }
                    else if(binding.searchView.toString().equals("")){
                        movieMainHomeList.clear();
                        mFilmAdapter.notifyDataSetChanged();
                        binding.rcvFilm.setAdapter(mFilmAdapter);
                        binding.tvTitle.setText("Bạn hãy nhập tên phim cần tìm!");
                        binding.tvTitle.setVisibility(View.VISIBLE);
                    }
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
            callApiSearchFilm(key, mCurrentPage);
            binding.loadSearchPage.setVisibility(View.VISIBLE);
            binding.loadMore.setVisibility(View.INVISIBLE);
            if(mCurrentPage==mTotalPage){
                mIsLastPage=true;
            }
        },3000);
    }
}
