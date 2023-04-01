package com.smile.watchmovie.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.ChannelActivity;
import com.smile.watchmovie.activity.VideoActivity;
import com.smile.watchmovie.adapter.VideoAdapter;
import com.smile.watchmovie.api.ApiServiceVideo;
import com.smile.watchmovie.custom.PaginationScrollListener;
import com.smile.watchmovie.database.Database;
import com.smile.watchmovie.databinding.FragmentHomeBinding;
import com.smile.watchmovie.databinding.FragmentIntroVideoBinding;
import com.smile.watchmovie.model.AppMedia;
import com.smile.watchmovie.model.AppMediaDetail;
import com.smile.watchmovie.model.ChannelDetail;
import com.smile.watchmovie.model.Video;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroVideoFragment extends Fragment {

    FragmentIntroVideoBinding binding;
    private VideoAdapter videoAdapter;
    private boolean mIsLastPage;
    Database database;
    private int mCurrentPage=0,idVideo;
    private boolean mIsLoading;
    private boolean isFollow=false,isLike=false,isDown=false,isShare=false,isFavorite=false;
    private Button btnFollowInDes;
    private LinearLayout lnInfoChannelInDes;
    private ImageView imvCancelx,imgvAvatarChannelInDes;
    private TextView txtNameVideo,txtNameChannelInDes,txtTotalLikeVideo,txtTotalViewVideo,txtTotalShareInDes,txtBodyDescribe,txtTotalFollowInDes;

    public IntroVideoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIntroVideoBinding.inflate(inflater, container, false);

        this.idVideo=getArguments().getInt("id_video");
        clickCallApiDetail();
        initView();
        onEvent();
        binding.pbLoadVideoRelated.setVisibility(View.VISIBLE);
        videoAdapter=new VideoAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcvListVideo.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rcvListVideo.addItemDecoration(itemDecoration);
        binding.rcvListVideo.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                mCurrentPage+=1;
                if(videoAdapter.getmListVideo().size()>=10*mCurrentPage){
                    mIsLoading=true;
                    binding.pbLoadVideo.setVisibility(View.VISIBLE);
                    loadNextPage();
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
        clickCallApiRelated(0);
        binding.rcvListVideo.setAdapter(videoAdapter);
        return binding.getRoot();
    }

    private void onEvent(){
        binding.imgvAvatarChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChannelActivity.class);
                intent.putExtra("id_channel",Integer.parseInt(binding.txtIdChannel.getText().toString()));
                startActivity(intent);
            }
        });

        binding.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFollow){
                    isFollow=true;
                    binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border_v2));
                    binding.btnFollow.setText("Hủy theo dõi");
                    binding.btnFollow.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                }else{
                    isFollow=false;
                    binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border));
                    binding.btnFollow.setText("Theo dõi");
                    binding.btnFollow.setTextColor(ContextCompat.getColor(getContext(), R.color.color_key));
                }
            }
        });

        binding.lnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShare==false){
                    isShare=true;
                    binding.imvShare.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_v2));
                    binding.txtShare.setTextColor(getResources().getColor(R.color.color_key));
                }else{
                    isShare=false;
                    binding.imvShare.setImageDrawable(getResources().getDrawable(R.drawable.ic_share));
                    binding.txtShare.setTextColor(getResources().getColor(R.color.gray));
                }

            }
        });

        binding.tvShowDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogMoreInfoVideo();
            }
        });

        binding.tvNameOfChannelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChannelActivity.class);
                intent.putExtra("id_channel",Integer.parseInt(binding.txtIdChannel.getText().toString()));
                startActivity(intent);
            }
        });

        binding.lnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike == false) {
                    binding.imvLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_film));
                    binding.txtLike.setTextColor(getResources().getColor(R.color.color_key));
                    isLike=true;
                }else{
                    binding.imvLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    binding.txtLike.setTextColor(getResources().getColor(R.color.gray));
                    isLike=false;
                }
            }
        });

        binding.lnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite == false) {
                    isFavorite=true;
                    binding.imvFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_added_favorite));
                    binding.txtFavorite.setTextColor(getResources().getColor(R.color.color_key));
                }else{
                    isFavorite=false;
                    binding.imvFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_favorite));
                    binding.txtFavorite.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        });

        binding.lnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDown == false) {
                    isDown=true;
                    binding.imvDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_film_downloaded));
                    binding.txtDownload.setTextColor(getResources().getColor(R.color.color_key));
                }else{
                    isDown=false;
                    binding.imvDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_download_film));
                    binding.txtDownload.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        });
    }

    private void showDialogMoreInfoVideo(){
        View viewDialog=getLayoutInflater().inflate(R.layout.dialog_show_more_info_video,null);
        initViewDis(viewDialog);
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(true);
        txtNameVideo.setText(binding.tvTitleOfVideo.getText().toString());
        txtNameChannelInDes.setText(binding.tvNameOfChannelVideo.getText().toString());
        txtBodyDescribe.setText(binding.txtBodyDisVideo.getText().toString());
        txtTotalViewVideo.setText(binding.txtTotalView.getText().toString());
        txtTotalLikeVideo.setText(binding.txtTotalLike.getText().toString());
        txtTotalShareInDes.setText(binding.txtTotalShare.getText().toString());
        txtTotalFollowInDes.setText(binding.txtTotalFollow.getText().toString()+" Lượt theo dõi");
        Glide.with(getContext()).load(binding.txtUrlAvatarChannel.getText().toString()).into(binding.imgvAvatarChannel);
        int channelId=Integer.parseInt(binding.txtIdChannel.getText().toString());
        lnInfoChannelInDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChannelActivity.class);
                intent.putExtra("id_channel",channelId);
                startActivity(intent);
            }
        });

        imgvAvatarChannelInDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChannelActivity.class);
                intent.putExtra("id_channel",channelId);
                startActivity(intent);
            }
        });

        imvCancelx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        btnFollowInDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFollow){
                    isFollow=true;
                    btnFollowInDes.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border_v2));
                    btnFollowInDes.setText("Hủy theo dõi");
                    btnFollowInDes.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                    binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border_v2));
                    binding.btnFollow.setText("Hủy theo dõi");
                    binding.btnFollow.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                }else{
                    isFollow=false;
                    btnFollowInDes.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border));
                    btnFollowInDes.setText("Theo dõi");
                    btnFollowInDes.setTextColor(ContextCompat.getColor(getContext(), R.color.color_key));
                    binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border));
                    binding.btnFollow.setText("Theo dõi");
                    binding.btnFollow.setTextColor(ContextCompat.getColor(getContext(), R.color.color_key));
                }
            }
        });


        BottomSheetBehavior bottomSheetBehavior=BottomSheetBehavior.from((View) viewDialog.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void clickCallApiChannel(int idChannel){
        //http://videoapi.kakoak.tls.tl/video-service/v1/channel/328/info?msisdn=%2B67075615473&timestamp=1611796455960&security=&clientType=Android&revision=15511
        ApiServiceVideo.apiServiceVideo.getChannelById(idChannel,"text","text","%2B67075615473","1611796455960","", "Android","15511").enqueue(new Callback<ChannelDetail>() {
            @Override
            public void onResponse(Call<ChannelDetail> call, Response<ChannelDetail> response) {
                ChannelDetail channelDetail =response.body();
                if(channelDetail!=null) {
                    database.FOLLOW(channelDetail.getData());
                }
            }
            @Override
            public void onFailure(Call<ChannelDetail> call, Throwable t) {
                Toast.makeText(getContext(), "Error get Channel is followed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void clickCallApiDetail() {
        //http://freeapi.kakoak.tls.tl/video-service/v1/video/165457/info?msisdn=0966409095&timestamp=123&security=123
        ApiServiceVideo.apiServiceVideo.getdatatodetail("en",idVideo,"0966409095",123, String.valueOf(123)).enqueue(new Callback<AppMediaDetail>() {
            @Override
            public void onResponse(Call<AppMediaDetail> call, Response<AppMediaDetail> response) {
                AppMediaDetail appMedia1=response.body();
                if(appMedia1!=null){
                    Video video= appMedia1.getData();
                    binding.tvTitleOfVideo.setText(video.getVideoTitle());
                    binding.tvNameOfChannelVideo.setText(video.getChannel().getChannelName());
                    binding.tvNumVideoChannel.setText(formatToTal(video.getChannel().getNumFollows())+" Người theo dõi");
                    String totalFollowChannel=formatToTal(video.getChannel().getNumFollows());
                    String totalView=formatToTal(video.getTotalViews());
                    String totalLike=formatToTal(video.getTotalLikes());
                    String totalShare=formatToTal(video.getTotalShares());
                    String totalComment=formatToTal(video.getTotalComments());
                    binding.tvChucnang.setText(totalView+" Lượt xem");
                    binding.txtBodyDisVideo.setText(video.getVideoDesc());
                    binding.txtTotalLike.setText(totalLike);
                    binding.txtTotalView.setText(totalView);
                    binding.txtTotalShare.setText(totalShare);
                    binding.txtTotalFollow.setText(totalFollowChannel);
                    Glide.with(getContext()).load(video.getChannel().getChannelAvatar()).into(binding.imgvAvatarChannel);
                    Glide.with(getContext()).load(video.getVideoImage())
                            .error(R.drawable.ic_baseline_broken_image_gray)
                            .placeholder(R.drawable.ic_baseline_image_gray)
                            .into(binding.imgvAvatarChannel);
                    binding.txtIdChannel.setText(String.valueOf(video.getChannelId()));
                    binding.txtUrlAvatarChannel.setText(video.getChannel().getChannelAvatar());
                    if(isFollow){
                        //binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.botron_followed));
                        binding.btnFollow.setText("Hủy theo dõi");
                    }else{
                        //binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.botron_edittext));
                        binding.btnFollow.setText("Theo dõi");
                    }
                }

            }

            @Override
            public void onFailure(Call<AppMediaDetail> call, Throwable t) {
                Toast.makeText(getContext(), "Error Get Video", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private String formatToTal(int data){
        return (data>=1000000)?((data/1000000)+"M"):((data<1000)? data+"":(data/1000)+"N");
    }

    private void initViewDis(View view){
        btnFollowInDes=view.findViewById(R.id.btn_follow_in_des);
        imvCancelx=view.findViewById(R.id.imv_cancelx);
        txtNameVideo=view.findViewById(R.id.txt_name_video);
        txtNameChannelInDes=view.findViewById(R.id.txt_name_channel_in_des);
        txtTotalLikeVideo=view.findViewById(R.id.txt_total_like_video);
        txtTotalViewVideo=view.findViewById(R.id.txt_total_view_video);
        txtTotalShareInDes=view.findViewById(R.id.txt_total_share_in_des);
        txtBodyDescribe=view.findViewById(R.id.txt_body_describe);
        txtTotalFollowInDes=view.findViewById(R.id.txt_total_follow_in_des);
        imgvAvatarChannelInDes=view.findViewById(R.id.imgv_avatar_channel_in_des);
        lnInfoChannelInDes=view.findViewById(R.id.ln_info_channel_in_des);
    }

    private void initView(){
        if (isLike) {
            binding.imvLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_film));
            binding.txtLike.setTextColor(getResources().getColor(R.color.color_key));
        }else{
            binding.imvLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
            binding.txtLike.setTextColor(getResources().getColor(R.color.gray));
        }

        if(isFollow){
            binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border_v2));
            binding.btnFollow.setText("Hủy theo dõi");
            binding.btnFollow.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
        }else{
            binding.btnFollow.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_border));
            binding.btnFollow.setText("Theo dõi");
            binding.btnFollow.setTextColor(ContextCompat.getColor(getContext(), R.color.color_key));
        }

        if(isShare){
            binding.imvShare.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_v2));
            binding.txtShare.setTextColor(getResources().getColor(R.color.color_key));
        }else{
            binding.imvShare.setImageDrawable(getResources().getDrawable(R.drawable.ic_share));
            binding.txtShare.setTextColor(getResources().getColor(R.color.gray));
        }

        if (isDown) {
            binding.imvDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_film_downloaded));
            binding.txtDownload.setTextColor(getResources().getColor(R.color.color_key));
        }else{
            binding.imvDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_download_film));
            binding.txtDownload.setTextColor(getResources().getColor(R.color.gray));
        }

        if (isFavorite) {
            binding.imvFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_added_favorite));
            binding.txtFavorite.setTextColor(getResources().getColor(R.color.color_key));
        }else{
            binding.imvFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_favorite));
            binding.txtFavorite.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    private void clickCallApiRelated(int page){
        //http://videoapi.kakoak.tls.tl/video-service/v1/video/6097/related?msisdn=0969633777&timestamp=123&security=123&page=0&size=10&lastHashId=13asd

        ApiServiceVideo.apiServiceVideo.getVideoRelated(idVideo,"0969633777","123", String.valueOf(123),page,10,"13asd","en").enqueue(new Callback<AppMedia>() {
            @Override
            public void onResponse(Call<AppMedia> call, Response<AppMedia> response) {

                List<Video> videoList=new ArrayList<>();
                AppMedia appMedia1=response.body();
                if(appMedia1!=null) {
                    binding.pbLoadVideoRelated.setVisibility(View.GONE);
                    if (mCurrentPage > 0) {
                        videoAdapter.setAddListVideo(appMedia1.getData());
                    } else {
                        videoAdapter.setData(appMedia1.getData());
                    }
                }

            }

            @Override
            public void onFailure(Call<AppMedia> call, Throwable t) {
                binding.pbLoadVideo.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Link API Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadNextPage(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsLoading=false;
                clickCallApiRelated(mCurrentPage);
                binding.pbLoadVideo.setVisibility(View.GONE);
            }
        },3000);
    }

}