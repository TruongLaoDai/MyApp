package com.smile.watchmovie.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.smile.watchmovie.adapter.VideoAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.api.ApiServiceVideo;
import com.smile.watchmovie.custom.PaginationScrollListener;
import com.smile.watchmovie.databinding.FragmentVideoChannelBinding;
import com.smile.watchmovie.model.AppMedia;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoChannelFragment extends Fragment {
    private int mCurrentPage=0;
    FragmentVideoChannelBinding binding;
    private VideoAdapter videoAdapter;
    private boolean mIsLoading;
    private boolean mIsLastPage;

    public VideoChannelFragment() {

    }


    public static VideoChannelFragment newInstance(String param1, String param2) {
        VideoChannelFragment fragment = new VideoChannelFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideoChannelBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment

        int idChannel=getArguments().getInt("id_channel");
        binding.pbLoadVideoChannel.setVisibility(View.VISIBLE);
        videoAdapter=new VideoAdapter(requireActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.rcvListVideo.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        binding.rcvListVideo.addItemDecoration(itemDecoration);
        binding.rcvListVideo.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                mCurrentPage+=1;
                if(videoAdapter.getmListVideo().size()>=20*mCurrentPage){
                    mIsLoading=true;
                    binding.pbLoadVideo.setVisibility(View.VISIBLE);
                    loadNextPage(idChannel);
                }else{
                    mCurrentPage-=1;
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
        clickCallApiVideoChannel(0,idChannel);
        videoAdapter.setActivity("home");
        videoAdapter.setType("mTypeLogin");
        binding.rcvListVideo.setAdapter(videoAdapter);

        return binding.getRoot();
    }

    private void clickCallApiVideoChannel(int page,int idChannel){
        //http://videoapi.kakoak.tls.tl/video-service/v1/video/10000/channel?msisdn=%2B67075600203&lastHashId=&page=0&size=20&timestamp=1611103216618&security=&clientType=Android&revision=15511

        ApiServiceVideo.apiServiceVideo.getListVideoByChannelId(idChannel,"text","text","%2B67075600203","",page, 20,"1611103216618","","Android","15511").enqueue(new Callback<AppMedia>() {
            @Override
            public void onResponse(Call<AppMedia> call, Response<AppMedia> response) {
                AppMedia appMedia1=response.body();
                if(appMedia1!=null) {
                    binding.pbLoadVideoChannel.setVisibility(View.GONE);
                    if (mCurrentPage > 0) {
                        videoAdapter.setAddListVideo(appMedia1.getData());
                    } else {
                        videoAdapter.setData(appMedia1.getData());
                    }
                }else{
                    binding.pbLoadVideoChannel.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "No video", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AppMedia> call, Throwable t) {
                Toast.makeText(getActivity(), "Link API Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadNextPage(int id){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading=false;
                clickCallApiVideoChannel(mCurrentPage,id);
                binding.pbLoadVideo.setVisibility(View.GONE);
            }
        },3000);
    }
}