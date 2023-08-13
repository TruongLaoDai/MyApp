package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Rational;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.WatchFilmViewPagerAdapter;
import com.smile.watchmovie.databinding.ActivityWatchFilmBinding;
import com.smile.watchmovie.listener.OnSwipeTouchListener;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.HistoryWatchFilm;
import com.smile.watchmovie.model.SubFilm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class WatchFilmActivity extends AppCompatActivity implements Player.Listener {
    private static final int MINIUM_DISTANCE = 100;
    private ActivityWatchFilmBinding binding;
    private ExoPlayer player;
    private boolean checkFullScreen = false;
    private boolean checkLockScreen = false;
    private ImageView ivUnlockScreen, brt_icon, vol_icon, ivFullScreen;
    private TextView tvAtEpisode, brt_text, vol_text;
    public FilmMainHome filmMainHome;
    private CollectionReference collectionReference;
    public String idUser, isVip;
    private int check = 0;

    private PictureInPictureParams.Builder piBuilder;
    private int device_width;
    private int device_height;
    private boolean left = false;
    private boolean right = false;
    private float baseX, baseY;

    private AudioManager adAudioManager;

    private ProgressBar brt_progress, vol_progress;
    private float speed;
    private PlaybackParameters playbackParameters;
    private Timer timer;
    private boolean auto_play, full_screen;
    private HistoryWatchFilm historyWatchFilm;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWatchFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Lấy dữ liệu phim được gửi sang */
        filmMainHome = new FilmMainHome();
        filmMainHome = (FilmMainHome) getIntent().getSerializableExtra("film");

        timer = new Timer();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");
        isVip = sharedPreferences.getString("isVip", "0");
        full_screen = sharedPreferences.getBoolean("full_screen", false);
        auto_play = sharedPreferences.getBoolean("auto_play", false);

        if (filmMainHome != null) {
            setUpView();
            initViewForTouchExoplayer();
        }

        if (full_screen) {
            setUpShowFilmFullScreen();
        }

        binding.exoplayerView.findViewById(R.id.iv_back).setOnClickListener(v -> onBackPressed());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        device_width = displayMetrics.widthPixels;
        device_height = displayMetrics.heightPixels;
    }

    private void setUpView() {
        /* Khởi tạo viewpager và tabLayout */
        WatchFilmViewPagerAdapter viewPagerAdapter = new WatchFilmViewPagerAdapter(this);
        binding.viewWatchFilePager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewWatchFilePager, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Giới thiệu");
                    break;
                case 1:
                    tab.setText("Bình luận");
                    break;
            }
        })).attach();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            piBuilder = new PictureInPictureParams.Builder();
        }
        setUpPlayer();

        if (!idUser.equals("")) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            collectionReference = firebaseFirestore.collection("WatchFilm");
            historyWatchFilm();
        }

        playFilmFirst();

        setUpCustomPlayFilm();

        setOnTouchExoplayer();

        ivFullScreen = binding.exoplayerView.findViewById(R.id.scaling);
        ivFullScreen.setOnClickListener(v -> {
            if (checkFullScreen) {
                setUpShowNoFullScreen();
            } else {
                setUpShowFilmFullScreen();
                binding.exoplayerView.findViewById(R.id.iv_back).setOnClickListener(v12 -> setUpShowNoFullScreen());
            }
        });
        ivUnlockScreen = binding.exoplayerView.findViewById(R.id.iv_unlock);
        ivUnlockScreen.setOnClickListener(v -> {
            if (!checkLockScreen) {
                ivUnlockScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_lock_24));
            } else {
                ivUnlockScreen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_round_lock_open_24));
            }
            checkLockScreen = !checkLockScreen;
            lockScreen(checkLockScreen);
        });

        binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setOnClickListener(v -> playSpeedFilm());

        binding.exoplayerView.findViewById(R.id.tv_speed_play_vertical).setOnClickListener(v -> playSpeedFilm());

        if (isVip.equals("0") && filmMainHome.getId() % 2 == 0) {
            setUpWhenVip();
        }

        if (!auto_play)
            player.setPlayWhenReady(false);
    }

    private void initViewForTouchExoplayer() {
        adAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        brt_progress = binding.exoplayerView.findViewById(R.id.brt_progress);
        brt_icon = binding.exoplayerView.findViewById(R.id.brt_icon);
        brt_text = binding.exoplayerView.findViewById(R.id.brt_text);
        vol_progress = binding.exoplayerView.findViewById(R.id.vol_progress);
        vol_icon = binding.exoplayerView.findViewById(R.id.vol_icon);
        vol_text = binding.exoplayerView.findViewById(R.id.vol_text);
    }

    private void setOnTouchExoplayer() {
        binding.exoplayerView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (event.getX() < ((double) device_width / 2)) {
                            left = true;
                            right = false;
                        } else if (event.getX() > ((double) device_width / 2)) {
                            left = false;
                            right = true;
                        }
                        baseX = event.getX();
                        baseY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (checkFullScreen) {
                            setSwipeWhenFullScreen(event);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        binding.exoplayerView.findViewById(R.id.vol_progress_container).setVisibility(View.GONE);
                        binding.exoplayerView.findViewById(R.id.brt_progress_container).setVisibility(View.GONE);
                        binding.exoplayerView.findViewById(R.id.vol_text_container).setVisibility(View.GONE);
                        binding.exoplayerView.findViewById(R.id.brt_text_container).setVisibility(View.GONE);
                        break;
                }
                return super.onTouch(v, event);
            }
        });
    }

    private void setSwipeWhenFullScreen(MotionEvent event) {
        long diffX = (long) Math.ceil(event.getX() - baseX);
        long diffY = (long) Math.ceil(event.getY() - baseY);

        double brightnessSpeed = 0.01;
        if (Math.abs(diffY) > MINIUM_DISTANCE) {
            if (Math.abs(diffY) > Math.abs(diffX)) {
                boolean value;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    value = Settings.System.canWrite(getApplicationContext());
                    if (value) {
                        if (left) {
                            ContentResolver mContentResolver = getContentResolver();
                            Window window = getWindow();
                            int brightness;
                            try {
                                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                brightness = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS);
                            } catch (Settings.SettingNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            int new_brightness = (int) (brightness - (diffY * brightnessSpeed));
                            if (new_brightness > 250) {
                                new_brightness = 250;
                            } else if (new_brightness < 1) {
                                new_brightness = 1;
                            }
                            double brt_percentage = Math.ceil(((double) new_brightness / (double) 250) * 100);
                            binding.exoplayerView.findViewById(R.id.brt_progress_container).setVisibility(View.VISIBLE);
                            binding.exoplayerView.findViewById(R.id.brt_text_container).setVisibility(View.VISIBLE);
                            brt_progress.setProgress((int) brt_percentage);

                            if (brt_percentage < 30) {
                                brt_icon.setImageResource(R.drawable.ic_brightness_low);
                            } else if (brt_percentage >= 30 && brt_percentage < 80) {
                                brt_icon.setImageResource(R.drawable.ic_brightness_half);
                            } else if (brt_percentage >= 80) {
                                brt_icon.setImageResource(R.drawable.ic_brightness);
                            }
                            String brightness_level = (int) brt_percentage + "%";
                            brt_text.setText(brightness_level);
                            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, new_brightness);
                            WindowManager.LayoutParams layoutParams = window.getAttributes();

                            layoutParams.screenBrightness = brightness / (float) 255;
                            window.setAttributes(layoutParams);
                        } else if (right) {
                            binding.exoplayerView.findViewById(R.id.vol_text_container).setVisibility(View.VISIBLE);
                            binding.exoplayerView.findViewById(R.id.vol_progress_container).setVisibility(View.VISIBLE);
                            int media_volume = adAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            int maxVol = adAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            double cal = (double) diffY * ((double) maxVol / ((double) (device_height * 2) - brightnessSpeed));
                            int newMediaVolume = media_volume - (int) cal;
                            if (newMediaVolume > maxVol) {
                                newMediaVolume = maxVol;
                            } else if (newMediaVolume < 1) {
                                newMediaVolume = 0;
                            }
                            adAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                            double volPer = Math.ceil(((double) newMediaVolume / (double) maxVol) * 100);
                            String volume_level = (int) volPer + "%";
                            vol_text.setText(volume_level);
                            if (volPer < 1) {
                                vol_icon.setImageResource(R.drawable.ic_volume_off);
                                vol_text.setVisibility(View.VISIBLE);
                                vol_text.setText(getString(R.string.off));
                            } else if (volPer >= 1) {
                                vol_icon.setImageResource(R.drawable.ic_volume);
                                vol_text.setVisibility(View.VISIBLE);
                            }
                            vol_progress.setProgress((int) volPer);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Allow write settings for swipe controls", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 111);
                    }
                }
            }
        }
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    public void playSpeedFilm() {
        Toast toast = new Toast(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.lout_custom_toast, findViewById(R.id.lout_toast));
        toast.setView(view);
        toast.setGravity(Gravity.LEFT | Gravity.TOP, 30, 350);
        toast.setDuration(Toast.LENGTH_SHORT);
        TextView tvToast = view.findViewById(R.id.tv_toast);

        TextView tvSpeed = binding.exoplayerView.findViewById(R.id.tv_speed_play_vertical);

        AlertDialog.Builder alBuilder = new AlertDialog.Builder(WatchFilmActivity.this);
        alBuilder.setTitle("Select playback speed");
        String[] items = {"0.5x", "1x Normal Speed", "1,25x", "1.5x", "2x"};
        int checkedItem = -1;
        alBuilder.setSingleChoiceItems(items, checkedItem, (dialog, which) -> {
            switch (which) {
                case 0:
                    speed = 0.5f;
                    tvToast.setText("0.5x");
                    toast.show();
                    binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setVisibility(View.GONE);
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("0.5x");
                    dialog.dismiss();
                    break;
                case 1:
                    speed = 1f;
                    tvToast.setText("1x");
                    toast.show();
                    binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setVisibility(View.GONE);
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("1x");
                    dialog.dismiss();
                    break;
                case 2:
                    speed = 1.25f;
                    tvToast.setText("1.25x");
                    toast.show();
                    binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setVisibility(View.GONE);
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("1.25x");
                    dialog.dismiss();
                    break;
                case 3:
                    speed = 1.5f;
                    tvToast.setText("1.5x");
                    toast.show();
                    binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setVisibility(View.GONE);
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("1.5x");
                    dialog.dismiss();
                    break;
                case 4:
                    speed = 2f;
                    tvToast.setText("2x");
                    toast.show();
                    binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setVisibility(View.GONE);
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("2x");
                    dialog.dismiss();
                    break;
                default:
                    dialog.dismiss();
                    break;
            }
            playbackParameters = new PlaybackParameters(speed);
            player.setPlaybackParameters(playbackParameters);
        });
        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    private void setUpShowFilmFullScreen() {
        binding.exoplayerView.findViewById(R.id.iv_episode_pre).setVisibility(View.VISIBLE);
        binding.exoplayerView.findViewById(R.id.iv_episode_next).setVisibility(View.VISIBLE);
        ivUnlockScreen.setVisibility(View.VISIBLE);
        binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setVisibility(View.GONE);
        binding.exoplayerView.findViewById(R.id.tv_speed_play_vertical).setVisibility(View.GONE);
        binding.exoplayerView.findViewById(R.id.back10s).setVisibility(View.VISIBLE);
        binding.exoplayerView.findViewById(R.id.next10s).setVisibility(View.VISIBLE);
        ivFullScreen.setImageDrawable(ContextCompat.getDrawable(WatchFilmActivity.this, R.drawable.ic_baseline_fullscreen_exit_24));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | Window.FEATURE_ACTION_BAR_OVERLAY);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.layoutFilm.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.layoutFilm.setLayoutParams(params);
        checkFullScreen = true;
    }

    private void setUpShowNoFullScreen() {
        binding.exoplayerView.findViewById(R.id.iv_episode_pre).setVisibility(View.GONE);
        binding.exoplayerView.findViewById(R.id.iv_episode_next).setVisibility(View.GONE);
        ivUnlockScreen.setVisibility(View.GONE);
        TextView tv_speed = binding.exoplayerView.findViewById(R.id.tv_speed_play_vertical);
        if (tv_speed.getText().length() == 0) {
            binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical).setVisibility(View.VISIBLE);
        } else {
            tv_speed.setVisibility(View.VISIBLE);
        }
        binding.exoplayerView.findViewById(R.id.back10s).setVisibility(View.GONE);
        binding.exoplayerView.findViewById(R.id.next10s).setVisibility(View.GONE);
        ivFullScreen.setImageDrawable(ContextCompat.getDrawable(WatchFilmActivity.this, R.drawable.ic_fullscreen));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.layoutFilm.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (230 * getApplicationContext().getResources().getDisplayMetrics().density);
        binding.layoutFilm.setLayoutParams(params);
        binding.exoplayerView.findViewById(R.id.iv_back).setOnClickListener(v1 -> onBackPressed());
        checkFullScreen = false;
    }

    private void setUpCustomPlayFilm() {
        FilmMainHome finalMovieMainHome = filmMainHome;
        binding.exoplayerView.findViewById(R.id.iv_episode_pre).setOnClickListener(v -> {
            int episode = Integer.parseInt(tvAtEpisode.getText().toString().substring(4, tvAtEpisode.getText().length())) - 1;
            if (episode > 0) {
                playFilm(finalMovieMainHome.getSubVideoList().get(episode - 1));
            }
        });
        binding.exoplayerView.findViewById(R.id.iv_episode_next).setOnClickListener(v -> {
            int episode = Integer.parseInt(tvAtEpisode.getText().toString().substring(4, tvAtEpisode.getText().length())) - 1;
            if (episode + 1 < finalMovieMainHome.getSubVideoList().size()) {
                playFilm(finalMovieMainHome.getSubVideoList().get(episode + 1));
            }
        });
        binding.exoplayerView.findViewById(R.id.next10s).setOnClickListener(v -> player.seekTo(player.getCurrentPosition() + 10000));
        binding.exoplayerView.findViewById(R.id.back10s).setOnClickListener(v -> player.seekTo(player.getCurrentPosition() - 10000));
    }

    private void playFilmFirst() {
        if (filmMainHome.getSubVideoList() != null) {
            Collections.sort(filmMainHome.getSubVideoList());
        } else {
            List<SubFilm> subVideoList = new ArrayList<>();
            SubFilm subFilm = new SubFilm();
            subFilm.setEpisode(1);
            subFilm.setLink(filmMainHome.getLink());
            subVideoList.add(subFilm);
            filmMainHome.setSubVideoList(subVideoList);
        }
        playFilm(filmMainHome.getSubVideoList().get(0));
    }

    private void setUpPlayer() {
        player = (new ExoPlayer.Builder(this)).build();
        PlayerView mPlayerView = binding.exoplayerView;
        mPlayerView.setPlayer(player);
        player.addListener(this);
        if (!auto_play)
            player.setPlayWhenReady(false);
    }

    private void setUpWhenVip() {
        player.setPlayWhenReady(false);

        DefaultTimeBar exoProgress = binding.exoplayerView.findViewById(R.id.exo_progress);
        ImageView exopPlay = binding.exoplayerView.findViewById(R.id.exo_play);
        exoProgress.setEnabled(false);
        exopPlay.setEnabled(false);
        openDialogWatchFilmAtTime("Bạn hãy đăng kí Vip để xem phim", 0, 2);
    }

    private void lockScreen(boolean check_lock_screen) {
        LinearLayout ll_play_stop_film = binding.exoplayerView.findViewById(R.id.layout_play_stop);
        RelativeLayout rl_root_layout = binding.exoplayerView.findViewById(R.id.rl_root_layout);
        LinearLayout ll_title = binding.exoplayerView.findViewById(R.id.ll_title);
        if (check_lock_screen) {
            ll_play_stop_film.setVisibility(View.INVISIBLE);
            rl_root_layout.setVisibility(View.INVISIBLE);
            ll_title.setVisibility(View.INVISIBLE);
        } else {
            ll_play_stop_film.setVisibility(View.VISIBLE);
            rl_root_layout.setVisibility(View.VISIBLE);
            ll_title.setVisibility(View.VISIBLE);
        }
    }

    public void playFilm(SubFilm subFilm) {
        for (SubFilm subVideo1 : filmMainHome.getSubVideoList()) {
            subVideo1.setWatching(subVideo1.getId() == subFilm.getId());
        }
        binding.exoplayerView.setVisibility(View.VISIBLE);
        binding.errorWatchFilm.setVisibility(View.GONE);
        String path = subFilm.getLink();
        Uri uri = Uri.parse(path);
        tvAtEpisode = binding.exoplayerView.findViewById(R.id.tv_at_episode);
        tvAtEpisode.setText(getString(R.string.tv_at_episode, subFilm.getEpisode()));
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        MediaSource mediaSource = buildMediaSource(uri);
        concatenatingMediaSource.addMediaSource(mediaSource);
        binding.exoplayerView.setPlayer(player);
        binding.exoplayerView.setKeepScreenOn(true);
        player.setPlaybackParameters(playbackParameters);
        player.prepare(concatenatingMediaSource);
        playError();
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        Player.Listener.super.onPlaybackStateChanged(playbackState);
        if (playbackState == Player.STATE_BUFFERING) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.exoplayerView.findViewById(R.id.layout_play_stop).setVisibility(View.INVISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            binding.exoplayerView.findViewById(R.id.layout_play_stop).setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Watch Movie"));
        return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
    }

    private void playError() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                binding.exoplayerView.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.errorWatchFilm.setVisibility(View.VISIBLE);
                Toast.makeText(WatchFilmActivity.this, "Film Playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!idUser.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            HistoryWatchFilm historyWatchFilm = new HistoryWatchFilm(filmMainHome.getId(), player.getCurrentPosition(), format.format(date), filmMainHome.getAvatar(), filmMainHome.getName());
            if (player.getCurrentPosition() > 30000) {
                if (check == 0) {
                    collectionReference.document("tblhistorywatchfilm").collection(idUser).add(historyWatchFilm);
                } else {
                    updateDataTimeWatchFilm(historyWatchFilm);
                }
            }
        }
        if (player.isPlaying()) {
            player.stop();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Rational asRational = new Rational(16, 9);
            piBuilder.setAspectRatio(asRational);
            enterPictureInPictureMode(piBuilder.build());
        }
        finish();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            binding.exoplayerView.hideController();
        } else {
            binding.exoplayerView.showController();
        }
    }

    private void updateDataTimeWatchFilm(HistoryWatchFilm historyWatchFilm) {
        collectionReference.document("tblhistorywatchfilm").collection(idUser)
                .document(this.historyWatchFilm.getDocumentID())
                .update("duration", historyWatchFilm.getDuration(),
                        "dayWatch", historyWatchFilm.getDayWatch());
    }

    private String messagePlayAtTime(long time) {
        int hour = (int) (time / 1000 / 3600);
        int minute = (int) ((time / 1000 - hour * 3600) / 60);
        int second = (int) (time / 1000 - hour * 3600 - minute * 60);
        return "Bạn có muốn xem phim từ " + hour + ":" + minute + ":" + second;
    }


    @SuppressLint("SetTextI18n")
    private void openDialogWatchFilmAtTime(String message, long time, int type) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_watch_film_from_time);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window == null) {
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

        tv_at_time.setText(message);

        dialog.show();

        btn_yes.setOnClickListener(v -> {
            if (type == 1) {
                player.setPlayWhenReady(true);
                player.seekTo(time);
            } else {
                Intent intent = new Intent(WatchFilmActivity.this, ChoosePaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            dialog.dismiss();
        });

        btn_no.setOnClickListener(v -> {
            if (type == 1) {
                player.setPlayWhenReady(true);
            } else {
                player.setPlayWhenReady(false);
                DefaultTimeBar exo_progress = binding.exoplayerView.findViewById(R.id.exo_progress);
                exo_progress.setEnabled(false);
            }
            dialog.dismiss();
        });
    }

    public void historyWatchFilm() {
        collectionReference.document("tblhistorywatchfilm").collection(idUser).whereEqualTo("id_film", filmMainHome.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        this.historyWatchFilm = doc.toObject(HistoryWatchFilm.class);
                        if (historyWatchFilm != null) {
                            historyWatchFilm.setDocumentID(doc.getId());
                            Date today = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String strToday = format.format(today);
                            try {
                                Date todayFormat = format.parse(strToday);
                                Date dayWatch = format.parse(historyWatchFilm.getDayWatch());
                                if (todayFormat != null) {
                                    if (todayFormat.compareTo(dayWatch) == 0) {
                                        check = 1;
                                    }
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            openDialogWatchFilmAtTime(messagePlayAtTime(historyWatchFilm.getDuration()), historyWatchFilm.getDuration(), 1);
                            player.setPlayWhenReady(false);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Get history film fail", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Configuration configuration = getResources().getConfiguration();

        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setUpShowNoFullScreen();
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setUpShowFilmFullScreen();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInPictureInPictureMode()) {
                player.setPlayWhenReady(true);
            } else {
                player.setPlayWhenReady(false);
                player.getPlaybackState();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUpPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        timer.cancel();
    }

    // Giải phóng ExoPlayer
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setUpShowFilmFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && !full_screen) {
            setUpShowNoFullScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            boolean value;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                value = Settings.System.canWrite(getApplicationContext());
                if (!value) {
                    Toast.makeText(getApplicationContext(), "Not Granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}