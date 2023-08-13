package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityShowMoreCategoryFilmBinding;
import com.smile.watchmovie.model.FilmArrayResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMoreCategoryFilmActivity extends AppCompatActivity {
    private ActivityShowMoreCategoryFilmBinding binding;
    private FilmSearchAdapter listFilmAdapter;
    private boolean mIsLoading = false;
    private int currentPage = 0;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowMoreCategoryFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryId = getIntent().getIntExtra("categoryId", 0);
        setupToolBar();

        listFilmAdapter = new FilmSearchAdapter(this);
        binding.rcvFilmCategory.setAdapter(listFilmAdapter);

        callApiGetByCategoryListMovie(categoryId, 0);

        binding.rcvFilmCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoading) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.rcvFilmCategory.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == listFilmAdapter.filmMainHomeList.size() - 1) {
                        mIsLoading = true;
                        currentPage += 1;
                        loadNextPage(categoryId);
                    }
                }
            }
        });

        binding.reloadLout.setOnRefreshListener(() -> {
            currentPage = 0;
            listFilmAdapter.clearData();
            callApiGetByCategoryListMovie(categoryId, currentPage);
        });
    }

    private void setupToolBar() {
        switch (categoryId) {
            case 14:
                binding.toolBar.setTitle("Hoạt hình");
                break;
            case 6:
                binding.toolBar.setTitle("Hành động");
                break;
            case 4:
                binding.toolBar.setTitle("Kinh dị");
                break;
            case 11:
                binding.toolBar.setTitle("Trinh thám");
                break;
            case 12:
                binding.toolBar.setTitle("Hài kịch");
                break;
            case 13:
                binding.toolBar.setTitle("Lãng mạn");
                break;
            case 15:
                binding.toolBar.setTitle("Phiêu lưu");
                break;
        }

        binding.toolBar.setNavigationOnClickListener(v -> finish());
    }

    public void callApiGetByCategoryListMovie(int categoryId, int page) {
        binding.loadHomePage.setVisibility(View.VISIBLE);
        ApiService.apiService.getFilmByCategory(getString(R.string.wsToken), categoryId, page, 10).enqueue(new Callback<FilmArrayResponse>() {
            @Override
            public void onResponse(@NonNull Call<FilmArrayResponse> call, @NonNull Response<FilmArrayResponse> response) {
                binding.loadHomePage.setVisibility(View.GONE);
                binding.reloadLout.setRefreshing(false);
                if (response.body() != null && response.body().getData() != null) {
                    listFilmAdapter.updateData(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
                binding.loadHomePage.setVisibility(View.GONE);
                binding.reloadLout.setRefreshing(false);
            }
        });
    }

    private void loadNextPage(int categoryId) {
        mIsLoading = false;
        callApiGetByCategoryListMovie(categoryId, currentPage);
    }
}