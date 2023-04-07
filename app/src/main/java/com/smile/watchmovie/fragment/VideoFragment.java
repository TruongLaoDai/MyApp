package com.smile.watchmovie.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.smile.watchmovie.adapter.VideoAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.api.ApiServiceVideo;
import com.smile.watchmovie.custom.PaginationScrollListener;
import com.smile.watchmovie.databinding.FragmentVideoBinding;
import com.smile.watchmovie.model.AppMedia;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoFragment extends Fragment {

    private boolean mIsLoading;
    private int mCurrentPage = 0;
    private VideoAdapter videoAdapter;
    private boolean mIsLastPage;
    private int idUser = 0;
    FragmentVideoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        //idUser = getArguments().getInt("Id_user");
        idUser=1;
        binding.pbLoadVideoHome.setVisibility(View.VISIBLE);
        videoAdapter = new VideoAdapter(requireActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.rcvListVideo.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        binding.rcvListVideo.addItemDecoration(itemDecoration);
        binding.rcvListVideo.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                mCurrentPage += 1;
                if (videoAdapter.getmListVideo().size() >= 8 * mCurrentPage) {
                    mIsLoading = true;
                    binding.pbLoadVideo.setVisibility(View.VISIBLE);
                    loadNextPage();
                } else {
                    mCurrentPage -= 1;
                }
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
        clickCallApi(0);
        videoAdapter.setActivity("home");
        videoAdapter.setType("mTypeLogin");
        binding.rcvListVideo.setAdapter(videoAdapter);
        return binding.getRoot();
    }

    private void clickCallApi(int page) {
        //http://freeapi.kakoak.tls.tl/video-service/v1/video/hot?msisdn=0969633777&timestamp=123&security=123&page=0&size=10&lastHashId=13asd

        ApiServiceVideo.apiServiceVideo.getdatatohome("en", "0969633777", 123, String.valueOf(123), page, 8, "13asd").enqueue(new Callback<AppMedia>() {
            @Override
            public void onResponse(Call<AppMedia> call, Response<AppMedia> response) {
                AppMedia appMedia1 = response.body();
                if (appMedia1 != null) {
                    binding.pbLoadVideoHome.setVisibility(View.GONE);
                    if (page > 0) {
                        binding.pbLoadVideo.setVisibility(View.GONE);
                        videoAdapter.setAddListVideo(appMedia1.getData());
                    } else {
                        videoAdapter.setData(appMedia1.getData());
                    }
                }

            }

            @Override
            public void onFailure(Call<AppMedia> call, Throwable t) {
                binding.pbLoadVideo.setVisibility(View.GONE);
                //Toast.makeText(HomeFragment.this, "Link API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading = false;
                clickCallApi(mCurrentPage);
                binding.pbLoadVideo.setVisibility(View.GONE);
            }
        }, 3000);
    }
}