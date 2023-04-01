package com.smile.watchmovie.activity;

import static android.net.Uri.parse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.HomeViewPagerAdapter;
import com.smile.watchmovie.adapter.VideoAdapter;
import com.smile.watchmovie.adapter.ViewPagerInfoVideoAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.api.ApiServiceVideo;
import com.smile.watchmovie.custom.HorizontalFlipTransformation;
import com.smile.watchmovie.custom.PaginationScrollListener;
import com.smile.watchmovie.database.Database;
import com.smile.watchmovie.databinding.ActivityVideoBinding;
import com.smile.watchmovie.model.AppMedia;
import com.smile.watchmovie.model.AppMediaDetail;
import com.smile.watchmovie.model.ChannelDetail;
import com.smile.watchmovie.model.Video;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    private Boolean mCheck=false;
    ActivityVideoBinding binding;
    ViewPagerInfoVideoAdapter adapter;
    private int idUser,idVideo;
    SimpleExoPlayer player;
    ConcatenatingMediaSource concatenatingMediaSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.idUser=getIntent().getIntExtra("id_user",-1);
        this.idVideo=getIntent().getIntExtra("id_video",-1);

        adapter = new ViewPagerInfoVideoAdapter(getSupportFragmentManager(), 2);
        adapter.setIdVideo(this.idVideo);
        binding.viewPager.setAdapter(adapter);
        binding.tab.setTabTextColors(Color.parseColor("#777776"), Color.parseColor("#000000"));
        binding.tab.setupWithViewPager(binding.viewPager);

        eventClick();
        clickCallApiDetail();
    }

    private void eventClick(){
        ImageView imgBack=findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView imgvFullScreen=binding.exoplayerView.findViewById(R.id.scaling);
        imgvFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCheck){
                    imgvFullScreen.setImageDrawable(ContextCompat.getDrawable(VideoActivity.this,R.drawable.fullscreen));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if(getSupportActionBar()!=null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) binding.rlPlayVideo.getLayoutParams();
                    params.width=params.MATCH_PARENT;
                    params.height=(int)(200*getApplicationContext().getResources().getDisplayMetrics().density);
                    binding.rlPlayVideo.setLayoutParams(params);
                    binding.layoutInfo.setVisibility(View.VISIBLE);
                    mCheck=false;
                }else{
                    imgvFullScreen.setImageDrawable(ContextCompat.getDrawable(VideoActivity.this,R.drawable.ic_baseline_fullscreen_exit_24));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if(getSupportActionBar()!=null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) binding.rlPlayVideo.getLayoutParams();
                    params.width=params.MATCH_PARENT;
                    params.height=params.MATCH_PARENT;
                    binding.layoutInfo.setVisibility(View.GONE);
                    binding.rlPlayVideo.setLayoutParams(params);
                    mCheck=true;
                }
            }
        });
    }

    private void playVideo(Video video){
        String path=video.getVideoMedia();
        Uri uri= parse(path);
        TextView txvTitle=binding.exoplayerView.findViewById(R.id.tv_title_video_play);
        txvTitle.setText(video.getVideoTitle());
        player=new SimpleExoPlayer.Builder(this).build();
        concatenatingMediaSource=new ConcatenatingMediaSource();
        MediaSource mediaSource=buildMediaSource(uri);
        concatenatingMediaSource.addMediaSource(mediaSource);
        binding.exoplayerView.setPlayer(player);
        binding.exoplayerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        playError();
    }
    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "YouMedia");
        return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
    }
    private  void playError(){
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(VideoActivity.this, "Video bị lỗi", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    private void clickCallApiDetail() {
        //http://freeapi.kakoak.tls.tl/video-service/v1/video/165457/info?msisdn=0966409095&timestamp=123&security=123
        ApiServiceVideo.apiServiceVideo.getdatatodetail("en",idVideo,"0966409095",123, String.valueOf(123)).enqueue(new Callback<AppMediaDetail>() {
            @Override
            public void onResponse(Call<AppMediaDetail> call, Response<AppMediaDetail> response) {
                AppMediaDetail appMedia1=response.body();
                if(appMedia1!=null){
                    Video video= appMedia1.getData();
                    playVideo(video);
                }

            }

            @Override
            public void onFailure(Call<AppMediaDetail> call, Throwable t) {
                Toast.makeText(VideoActivity.this, "Không thể tải video", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(player.isPlaying()){
            player.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }
}