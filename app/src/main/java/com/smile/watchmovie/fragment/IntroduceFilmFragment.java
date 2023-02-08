package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.adapter.EpisodeAdapter;
import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.custom.PaginationScrollListener;
import com.smile.watchmovie.databinding.FragmentIntroduceFilmBinding;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.SubFilm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IntroduceFilmFragment extends Fragment {

    private FragmentIntroduceFilmBinding binding;
    private WatchFilmActivity mWatchFilmActivity;
    private FilmSearchAdapter mFilmSearchAdapter;
    private List<FilmMainHome> mFilmList;
    private FilmMainHome filmMainHome;
    private EpisodeAdapter mEpisodeAdapter;
    private boolean mIsLoading;
    private boolean mIsLastPage;
    private int mCurrentPage=0;
    private final int mTotalPage=20;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mWatchFilmActivity = (WatchFilmActivity) getActivity();
        binding = FragmentIntroduceFilmBinding.inflate(inflater, container, false);

        filmMainHome = new FilmMainHome();
        filmMainHome = (FilmMainHome) mWatchFilmActivity.getIntent().getSerializableExtra("movie");

        if(filmMainHome != null) {
            binding.tvNameFilm.setText(filmMainHome.getName());
            binding.tvViewNumber.setText(filmMainHome.getViewNumber() + " Lượt xem");

            mEpisodeAdapter = new EpisodeAdapter(mWatchFilmActivity, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mWatchFilmActivity, RecyclerView.HORIZONTAL, false);
            binding.rcvEpisode.setLayoutManager(layoutManager);

            playFilmFirst();
        }

        mFilmList = new ArrayList<>();

        mFilmSearchAdapter = new FilmSearchAdapter(mWatchFilmActivity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mWatchFilmActivity, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(mWatchFilmActivity, DividerItemDecoration.VERTICAL);
        binding.rcvMore.setLayoutManager(linearLayoutManager);
        binding.rcvMore.addItemDecoration(itemDecoration);
        //binding.loadMore.setVisibility(View.VISIBLE);

        binding.rcvMore.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                mIsLoading=true;
                //binding.loadMore.setVisibility(View.VISIBLE);
                mCurrentPage+=1;
                loadNextPage(filmMainHome.getCategoryId());
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

        callApiGetByCategoryListMovie(filmMainHome.getCategoryId(), 0);
        return binding.getRoot();
    }

    private void playFilmFirst() {
        if(filmMainHome.getSubVideoList()!=null) {
            binding.loutEpisode.setVisibility(View.VISIBLE);
            Collections.sort(filmMainHome.getSubVideoList());
            mEpisodeAdapter.setData(filmMainHome.getSubVideoList());
        }
        else{
            List<SubFilm> subVideoList = new ArrayList<>();
            SubFilm subFilm = new SubFilm();
            binding.loutEpisode.setVisibility(View.INVISIBLE);
            subFilm.setEpisode(1);
            subFilm.setLink(filmMainHome.getLink());
            subVideoList.add(subFilm);
            filmMainHome.setSubVideoList(subVideoList);
            mEpisodeAdapter.setData(subVideoList);
        }
        binding.rcvEpisode.setAdapter(mEpisodeAdapter);
    }

    public void episodeFilmPlaying(SubFilm subFilm) {
        for (SubFilm subVideo1 : filmMainHome.getSubVideoList()) {
            subVideo1.setWatching(subVideo1.getId() == subFilm.getId());
        }
        mEpisodeAdapter.setData(filmMainHome.getSubVideoList());
    }

    public void callApiGetByCategoryListMovie(int categoryId, int page) {
        ApiService.apiService.getFilmByCategory("7da353b8a3246f851e0ee436d898a26d", categoryId, page, 5).enqueue(new Callback<FilmArrayResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<FilmArrayResponse> call, @NonNull Response<FilmArrayResponse> response) {
                FilmArrayResponse movieArrayResponse = response.body();
                if (movieArrayResponse != null) {
//                    if(binding.loadHomePage.getVisibility() == View.VISIBLE){
//                        binding.loadHomePage.setVisibility(View.INVISIBLE);
//                    }
//                    if(binding.loadMore.getVisibility() == View.VISIBLE) {
//                        binding.loadMore.setVisibility(View.INVISIBLE);
//                    }
                    if(movieArrayResponse.getData() != null) {
                        mFilmList.addAll(movieArrayResponse.getData());
                    }else{
                        mIsLastPage = true;
                        Toast.makeText(mWatchFilmActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                    }
                    if (page == 0) {
                        mFilmSearchAdapter.setData(mFilmList);
                        binding.rcvMore.setAdapter(mFilmSearchAdapter);
                    }
                    mFilmSearchAdapter.notifyDataSetChanged();
                    if (mFilmList.size() > 0 && movieArrayResponse.getData().size() == 0) {
                        mIsLastPage = true;
                        Toast.makeText(mWatchFilmActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                    }
                } else{
                    mIsLastPage = true;
                    Toast.makeText(mWatchFilmActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
                //binding.loadMore.setVisibility(View.INVISIBLE);
                Toast.makeText(mWatchFilmActivity, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage(int categoryId){
        Handler handler=new Handler();
        handler.postDelayed(() -> {
            mIsLoading=false;
            callApiGetByCategoryListMovie(categoryId, mCurrentPage);
            if(mCurrentPage==mTotalPage){
                mIsLastPage=true;
            }
        },3500);
    }
}