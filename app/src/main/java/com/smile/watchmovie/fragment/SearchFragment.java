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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.custom.PaginationScrollListener;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentSearchBinding;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private FilmSearchAdapter mFilmSearchAdapter;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage = 0;
    private final int mTotalPage = 20;
    private List<FilmMainHome> movieMainHomeList;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        mFilmSearchAdapter = new FilmSearchAdapter(requireActivity());
        movieMainHomeList = new ArrayList<>();
        binding.searchView.clearFocus();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    binding.tvTitle.setVisibility(View.VISIBLE);
                    binding.loadSearchPage.setVisibility(View.VISIBLE);
                    binding.rcvFilm.setVisibility(View.GONE);
                    movieMainHomeList.clear();
                    mFilmSearchAdapter.setData(movieMainHomeList);
                } else {
                    binding.tvTitle.setVisibility(View.GONE);
                    binding.loadSearchPage.setVisibility(View.VISIBLE);
                    callApiSearchFilm(query, 0);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        binding.rcvFilm.setLayoutManager(linearLayoutManager);
        binding.rcvFilm.addItemDecoration(itemDecoration);
        binding.rcvFilm.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;
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
        ApiService.apiService.searchFilms("7da353b8a3246f851e0ee436d898a26d", key, page, 10).enqueue(new Callback<FilmArrayResponse>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<FilmArrayResponse> call, @NonNull Response<FilmArrayResponse> response) {
                binding.loadSearchPage.setVisibility(View.GONE);
                FilmArrayResponse movieArrayResponse = response.body();
                if (movieArrayResponse != null) {
                    if (!key.equals("")) {
                        if (binding.loadSearchPage.getVisibility() == View.VISIBLE) {
                            binding.loadSearchPage.setVisibility(View.INVISIBLE);
                        }
                        movieMainHomeList.addAll(movieArrayResponse.getData());
                        if (page == 0) {
                            mFilmSearchAdapter.setData(movieMainHomeList);
                            binding.rcvFilm.setAdapter(mFilmSearchAdapter);
                        }
                        mFilmSearchAdapter.notifyDataSetChanged();
                        if (movieMainHomeList.size() > 0 && movieArrayResponse.getData().size() == 0) {
                            Toast.makeText(requireActivity(), "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                        } else if (movieMainHomeList.size() == 0) {
                            binding.tvTitle.setVisibility(View.VISIBLE);
                            binding.tvTitle.setText("Không có phim bạn cần tìm!");
                        }
                    } else if (binding.searchView.toString().equals("")) {
                        movieMainHomeList.clear();
                        mFilmSearchAdapter.notifyDataSetChanged();
                        binding.rcvFilm.setAdapter(mFilmSearchAdapter);
                        binding.tvTitle.setText("Bạn hãy nhập tên phim cần tìm!");
                        binding.tvTitle.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireActivity(), "Tìm kiếm không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mIsLoading = false;
            callApiSearchFilm("key", mCurrentPage);
            if (mCurrentPage == mTotalPage) {
                mIsLastPage = true;
            }
        }, 3000);
    }
}
