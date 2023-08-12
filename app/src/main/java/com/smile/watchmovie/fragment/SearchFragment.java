package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.custom.PaginationScrollListener;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentSearchBinding;
import com.smile.watchmovie.model.FilmArrayResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private FilmSearchAdapter mFilmSearchAdapter;
    private boolean mIsLoading, mIsLastPage;
    private int currentPage = 0;
    private String keySearch;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        mFilmSearchAdapter = new FilmSearchAdapter(requireActivity());
        binding.rcvFilm.setAdapter(mFilmSearchAdapter);
        binding.searchView.clearFocus();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().equals("")) {
                    keySearch = query;
                    currentPage = 0;
                    binding.tvTitle.setVisibility(View.GONE);
                    mFilmSearchAdapter.clearData();
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
        binding.rcvFilm.setLayoutManager(linearLayoutManager);
        binding.rcvFilm.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                mIsLoading = true;
                currentPage += 1;
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
            @Override
            public void onResponse(@NonNull Call<FilmArrayResponse> call, @NonNull Response<FilmArrayResponse> response) {
                binding.loadSearchPage.setVisibility(View.GONE);
                if (response.body() != null) {
                    mFilmSearchAdapter.updateData(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
                binding.loadSearchPage.setVisibility(View.GONE);
                binding.tvTitle.setVisibility(View.VISIBLE);
                Toast.makeText(requireActivity(), "Tìm kiếm không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage() {
        mIsLoading = false;
        callApiSearchFilm(keySearch, currentPage);
        if (currentPage == 20) {
            mIsLastPage = true;
        }
    }
}