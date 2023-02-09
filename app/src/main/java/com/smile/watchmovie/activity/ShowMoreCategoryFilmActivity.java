package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityShowMoreCategoryFilmBinding;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMoreCategoryFilmActivity extends AppCompatActivity {

    private ActivityShowMoreCategoryFilmBinding binding;
    private FilmSearchAdapter mFilmSearchAdapter;
    private boolean mIsLoading = false;
    private int mCurrentPage = 0;
    private int categoryId;
    private List<FilmMainHome> mFilmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_more_category_film);

        binding = ActivityShowMoreCategoryFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFilmList = new ArrayList<>();

        mFilmSearchAdapter = new FilmSearchAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvFilmCategory.setLayoutManager(linearLayoutManager);
        binding.rcvFilmCategory.addItemDecoration(itemDecoration);

        categoryId = getIntent().getIntExtra("categoryId", 0);

        setupToolBar();

        binding.rcvFilmCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoading) {
                    LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) binding.rcvFilmCategory.getLayoutManager();
                    if (linearLayoutManager1 != null && linearLayoutManager1.findLastCompletelyVisibleItemPosition() == mFilmList.size() - 1) {
                        mIsLoading = true;
                        binding.loadMore.setVisibility(View.VISIBLE);
                        mCurrentPage += 1;
                        loadNextPage(categoryId);
                    }
                }
            }
        });

        callApiGetByCategoryListMovie(categoryId, 0);
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
        binding.toolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolBar.setNavigationOnClickListener(v -> finish());
    }

    public void callApiGetByCategoryListMovie(int categoryId, int page) {
        ApiService.apiService.getFilmByCategory("7da353b8a3246f851e0ee436d898a26d", categoryId, page, 5).enqueue(new Callback<FilmArrayResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<FilmArrayResponse> call, @NonNull Response<FilmArrayResponse> response) {
                FilmArrayResponse movieArrayResponse = response.body();
                if (movieArrayResponse != null) {
                    if (binding.loadHomePage.getVisibility() == View.VISIBLE) {
                        binding.loadHomePage.setVisibility(View.INVISIBLE);
                    }
                    if (binding.loadMore.getVisibility() == View.VISIBLE) {
                        binding.loadMore.setVisibility(View.INVISIBLE);
                    }
                    if (movieArrayResponse.getData() != null) {
                        mFilmList.addAll(movieArrayResponse.getData());
                    } else {
                        Toast.makeText(ShowMoreCategoryFilmActivity.this, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                    }
                    if (page == 0) {
                        mFilmSearchAdapter.setData(mFilmList);
                        binding.rcvFilmCategory.setAdapter(mFilmSearchAdapter);
                    }
                    mFilmSearchAdapter.notifyDataSetChanged();
                    if (mFilmList.size() > 0 && movieArrayResponse.getData().size() == 0) {
                        Toast.makeText(ShowMoreCategoryFilmActivity.this, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ShowMoreCategoryFilmActivity.this, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
                binding.loadMore.setVisibility(View.INVISIBLE);
                Toast.makeText(ShowMoreCategoryFilmActivity.this, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage(int categoryId) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mIsLoading = false;
            callApiGetByCategoryListMovie(categoryId, mCurrentPage);
        }, 3500);
    }
}