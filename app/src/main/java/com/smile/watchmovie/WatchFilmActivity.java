package com.smile.watchmovie;

import static android.net.Uri.parse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.adapter.EpisodeAdapter;
import com.smile.watchmovie.databinding.ActivityWatchFilmBinding;
import com.smile.watchmovie.model.MovieMainHome;
import com.smile.watchmovie.model.SubVideo;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WatchFilmActivity extends AppCompatActivity implements Player.Listener{

    private ActivityWatchFilmBinding binding;
    private ExoPlayer player;
    private boolean checkFullScreen = false;
    private boolean checkLockScreen = false;
    private ImageView ivUnlockScreen;
    private TextView tvAtEpisode;
    private MovieMainHome movieMainHome;
    private EpisodeAdapter mEpisodeAdapter;
    private CollectionReference collectionReference;
    private String idUser;
    private ImageView ivFullScreen;
    private int check = 0;
    private long timePreviod = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_film);

        binding = ActivityWatchFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieMainHome = new MovieMainHome();
        movieMainHome = (MovieMainHome) getIntent().getSerializableExtra("movie");

        binding.tvNameFilm.setText(movieMainHome.getName());
        binding.tvViewNumber.setText(movieMainHome.getViewNumber()+" Lượt xem");
        binding.tvDescription.setText(movieMainHome.getDescription());

        getIdUser();
        setUpPlayer();

        mEpisodeAdapter = new EpisodeAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        binding.rcvEposide.setLayoutManager(layoutManager);

        if(!idUser.equals("")) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            collectionReference = firebaseFirestore.collection("history_watch_film_" + idUser);

            historyWatchFilm();
        }

        playFilmFirst();

        setUpCustomPlayFilm();

        ivFullScreen = binding.exoplayerView.findViewById(R.id.scaling);
        ivFullScreen.setOnClickListener(v -> {
            if(checkFullScreen){
                setUpShowNoFullScreen();

            }else{
                setUpShowFilmFullScreen();
                binding.exoplayerView.findViewById(R.id.iv_back).setOnClickListener(v12 -> setUpShowNoFullScreen());
            }
        });
        ivUnlockScreen = binding.exoplayerView.findViewById(R.id.iv_unlock);
        ivUnlockScreen.setOnClickListener(v -> {
            if(!checkLockScreen){
                ivUnlockScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_lock_24));
            }
            else{
                ivUnlockScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_round_lock_open_24));
            }
            checkLockScreen = !checkLockScreen;
            lockScreen(checkLockScreen);
        });

        binding.exoplayerView.findViewById(R.id.iv_back).setOnClickListener(v -> backOther());
    }

    private void setUpShowFilmFullScreen() {
        ivFullScreen.setImageDrawable(ContextCompat.getDrawable(WatchFilmActivity.this,R.drawable.ic_baseline_fullscreen_exit_24));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | Window.FEATURE_ACTION_BAR_OVERLAY);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) binding.layoutFilm.getLayoutParams();

        params.width= ViewGroup.LayoutParams.MATCH_PARENT;
        params.height= ViewGroup.LayoutParams.MATCH_PARENT;
        binding.layoutFilm.setLayoutParams(params);
        checkFullScreen=true;
    }

    private void setUpShowNoFullScreen() {
        ivFullScreen.setImageDrawable(ContextCompat.getDrawable(WatchFilmActivity.this,R.drawable.fullscreen));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if(getSupportActionBar()!=null){
            getSupportActionBar().show();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) binding.layoutFilm.getLayoutParams();
        params.width= ViewGroup.LayoutParams.MATCH_PARENT;
        params.height=(int)(230*getApplicationContext().getResources().getDisplayMetrics().density);
        binding.layoutFilm.setLayoutParams(params);
        binding.exoplayerView.findViewById(R.id.iv_back).setOnClickListener(v1 -> finish());
        checkFullScreen=false;
    }

    private void setUpCustomPlayFilm() {
        MovieMainHome finalMovieMainHome = movieMainHome;
        binding.exoplayerView.findViewById(R.id.iv_episode_pre).setOnClickListener(v -> {
            int episode = Integer.parseInt(tvAtEpisode.getText().toString().substring(4, tvAtEpisode.getText().length()))-1;
            if(episode>0){
                playVideo(finalMovieMainHome.getSubVideoList().get(episode-1));
            }
        });
        binding.exoplayerView.findViewById(R.id.iv_episode_next).setOnClickListener(v -> {
            int episode = Integer.parseInt(tvAtEpisode.getText().toString().substring(4, tvAtEpisode.getText().length()))-1;
            if(episode+1< finalMovieMainHome.getSubVideoList().size()){
                playVideo(finalMovieMainHome.getSubVideoList().get(episode+1));
            }
        });
        binding.exoplayerView.findViewById(R.id.next10s).setOnClickListener(v -> player.seekTo(player.getCurrentPosition() + 10000));
        binding.exoplayerView.findViewById(R.id.back10s).setOnClickListener(v -> player.seekTo(player.getCurrentPosition() - 10000));
    }

    private void playFilmFirst() {
        if(movieMainHome.getSubVideoList()!=null) {
            Collections.sort(movieMainHome.getSubVideoList());
            mEpisodeAdapter.setData(movieMainHome.getSubVideoList());
        }
        else{
            List<SubVideo> subVideoList = new ArrayList<>();
            SubVideo subVideo = new SubVideo();
            subVideo.setEpisode(1);
            subVideo.setLink(movieMainHome.getLink());
            subVideoList.add(subVideo);
            movieMainHome.setSubVideoList(subVideoList);
            mEpisodeAdapter.setData(subVideoList);
        }
        binding.rcvEposide.setAdapter(mEpisodeAdapter);
        playVideo(movieMainHome.getSubVideoList().get(0));
    }

    private void backOther(){
        finish();
        if(!idUser.equals("")) {
            Map<String, Object> historyWatchFilm = new HashMap<>();
            historyWatchFilm.put("idFilm", movieMainHome.getId() + "");
            historyWatchFilm.put("time", player.getCurrentPosition() + "");
            if (player.getCurrentPosition() > 30000 && check == 0) {
                collectionReference.add(historyWatchFilm);
            } else if (check == 1) {
                updateDataTimeWatchFilm(historyWatchFilm);
            }
        }
        if(player.isPlaying()){
            player.stop();
        }
    }

    private void getIdUser(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(acct == null && accessToken == null){
            idUser = "";
        }
        else if(acct != null){
            this.idUser = acct.getId();
        }
        else if(!accessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    (object, response) -> {
                        // Application code
                        try {
                            assert object != null;
                            this.idUser = (String) object.get("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    private void setUpPlayer(){
        player=(new ExoPlayer.Builder(this)).build();
        PlayerView mPlayerView = binding.exoplayerView;
        mPlayerView.setPlayer(player);
        player.addListener(this);
    }

    private void lockScreen(boolean check_lock_screen) {
        LinearLayout ll_play_stop_film = binding.exoplayerView.findViewById(R.id.layout_play_stop);
        RelativeLayout rl_root_layout = binding.exoplayerView.findViewById(R.id.rl_root_layout);
        LinearLayout ll_title = binding.exoplayerView.findViewById(R.id.ll_title);
        if(check_lock_screen){
            ll_play_stop_film.setVisibility(View.INVISIBLE);
            rl_root_layout.setVisibility(View.INVISIBLE);
            ll_title.setVisibility(View.INVISIBLE);
        }
        else{
            ll_play_stop_film.setVisibility(View.VISIBLE);
            rl_root_layout.setVisibility(View.VISIBLE);
            ll_title.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void playVideo(SubVideo subVideo){
        for(SubVideo subVideo1 : movieMainHome.getSubVideoList()){
            subVideo1.setWatching(subVideo1.getId() == subVideo.getId());
        }
        mEpisodeAdapter.setData(movieMainHome.getSubVideoList());
        String path = subVideo.getLink();
        Uri uri= parse(path);
        tvAtEpisode = binding.exoplayerView.findViewById(R.id.tv_at_episode);
        tvAtEpisode.setText(getString(R.string.tv_at_episode,subVideo.getEpisode()));
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        MediaSource mediaSource=buildMediaSource(uri);
        concatenatingMediaSource.addMediaSource(mediaSource);
        binding.exoplayerView.setPlayer(player);
        binding.exoplayerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        playError();
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        Player.Listener.super.onPlaybackStateChanged(playbackState);
        if(playbackState == Player.STATE_BUFFERING){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.exoplayerView.findViewById(R.id.layout_play_stop).setVisibility(View.INVISIBLE);
        }
        else if(playbackState == Player.STATE_READY){
            binding.exoplayerView.findViewById(R.id.layout_play_stop).setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "Watch Movie");
        return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
    }

    private  void playError(){
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Toast.makeText(WatchFilmActivity.this, "Film Playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!idUser.equals("")) {
            Map<String, Object> historyWatchFilm = new HashMap<>();
            historyWatchFilm.put("idFilm", movieMainHome.getId() + "");
            historyWatchFilm.put("time", player.getCurrentPosition() + "");
            if (player.getCurrentPosition() > 30000 && check == 0) {
                collectionReference.add(historyWatchFilm);
            } else if (check == 1) {
                updateDataTimeWatchFilm(historyWatchFilm);
            }
        }
        if(player.isPlaying()){
            player.stop();
        }
    }

    private void updateDataTimeWatchFilm(Map<String, Object> historyWatchFilm) {
        collectionReference.whereEqualTo("idFilm", movieMainHome.getId()+"")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        collectionReference.document(documentId)
                                .update(historyWatchFilm);
                    }
                });
    }


    @SuppressLint("SetTextI18n")
    private void openDialogWatchFilmAtTime(long time){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_watch_film_from_time);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        TextView tv_at_time = dialog.findViewById(R.id.tv_at_time);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);

        int hour = (int) (time / 1000 / 3600);
        int minute = (int) ((time / 1000 - hour * 3600)/60);
        int second = (int) (time / 1000 - hour * 3600 - minute * 60);
        String atTime = hour + ":" + minute + ":" + second;
        tv_at_time.setText("Bạn có muốn xem phim từ " + atTime);

        btn_yes.setOnClickListener(v -> {
            player.setPlayWhenReady(true);
            player.seekTo(time);
            dialog.dismiss();
        });

        btn_no.setOnClickListener(v ->{
            player.setPlayWhenReady(true);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void historyWatchFilm(){
        collectionReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot snapshot = task.getResult();
                for(QueryDocumentSnapshot doc : snapshot){
                    String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                    if(Integer.parseInt(idFilm1) == movieMainHome.getId()){
                        String time = Objects.requireNonNull(doc.get("time")).toString();
                        check = 1;
                        timePreviod = Long.parseLong(time);
                        openDialogWatchFilmAtTime(Long.parseLong(time));
                        player.setPlayWhenReady(false);
                    }
                }
            }
        });
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

    @Override
    protected void onStop() {
        super.onStop();
        player.release();
    }
}