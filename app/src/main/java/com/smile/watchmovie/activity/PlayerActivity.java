package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.WatchFilmViewPagerAdapter;
import com.smile.watchmovie.databinding.ActivityWatchFilmBinding;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.HistoryWatchFilm;
import com.smile.watchmovie.model.SubFilm;
import com.smile.watchmovie.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity implements Player.Listener {
    private ActivityWatchFilmBinding binding;
    public FilmMainHome film;
    private ExoPlayer player;
    private ImageView fullScreen;
    private TextView tvSpeed;
    private CollectionReference collectionReference;
    public String idUser, isVip;
    private boolean isFullScreen = false, existHistory = false;
    private float speed;
    private boolean auto_play, full_screen;
    private HistoryWatchFilm historyWatchFilm;
    private int markerSpeedPlayFilm = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWatchFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Lấy dữ liệu phim được gửi sang */
        film = (FilmMainHome) getIntent().getSerializableExtra("film");

        /* Lấy thông tin user và các cài đặt */
        SharedPreferences sharedPreferences = getSharedPreferences(
                Constant.NAME_DATABASE_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
        );
        idUser = sharedPreferences.getString(Constant.ID_USER, "");
        isVip = sharedPreferences.getString(Constant.IS_VIP, "0");
        full_screen = sharedPreferences.getBoolean(Constant.FULL_SCREEN, false);
        auto_play = sharedPreferences.getBoolean(Constant.AUTO_PLAY, true);

        /* mapping from custom_playback_view.xml */
        fullScreen = binding.exoplayerView.findViewById(R.id.ivFullscreen);
        ImageView speedPlayVideo = binding.exoplayerView.findViewById(R.id.iv_speed_play_vertical);
        tvSpeed = binding.exoplayerView.findViewById(R.id.tv_speed_play_vertical);

        /* Thiết lập dữ liệu */
        if (film != null) {
            setupFirebase();
            prepareVideo();
            controllerTimeVideo();
            setFullScreen();
            initialInfoRelate();
            speedPlayVideo.setOnClickListener(view -> playSpeedFilm());
        }
    }

    private void setupFirebase() {
        if (!idUser.equals("")) {
            /* Khởi tạo dữ liệu firebase */
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            collectionReference = firebaseFirestore.collection(Constant.FirebaseFiretore.NAME_DATABASE);
        }
    }

    private void prepareVideo() {
        if ((idUser == null || idUser.equals("")) && film.getId() % 2 == 0) {
            showDialog("Bạn cần đăng nhập tài khoản và đăng ký tài khoản vip (Nếu chưa có) để có thể xem nội dung bộ phim này. (Hãy vào mục Hồ sơ để thực hiện)");
        } else if (isVip.equals("0") && film.getId() % 2 == 0) {
            showDialog("Bạn cần đăng ký tài khoản vip để có thể xem nội dung bộ phim này. (Hãy vào mục Hồ sơ để thực hiện đăng ký)");
        } else {
            /* Khởi tạo trình phát */
            setUpPlayer();
        }
    }

    private void setUpPlayer() {
        player = new ExoPlayer.Builder(this).build();
        binding.exoplayerView.setPlayer(player);

        /* Phát phim */
        playFilmFirst();
    }

    private void playFilmFirst() {
        if (film.getSubVideoList() != null) {
            Collections.sort(film.getSubVideoList());
            playFilm(film.getSubVideoList().get(0));
        } else {
            SubFilm subFilm = new SubFilm();
            subFilm.setLink(film.getLink());
            playFilm(subFilm);
        }
    }

    public void playFilm(SubFilm subFilm) {
        if (player.isPlaying()) {
            player.stop();
        }

        MediaItem mediaItem = MediaItem.fromUri(subFilm.getLink());
        player.setMediaItem(mediaItem);
        player.prepare();

        loadTimeWatched();
    }

    private void controllerTimeVideo() {
        binding.exoplayerView.findViewById(R.id.forward).setOnClickListener(v -> player.seekTo(player.getCurrentPosition() + 10000));
        binding.exoplayerView.findViewById(R.id.rewind).setOnClickListener(v -> player.seekTo(player.getCurrentPosition() - 10000));
    }

    private void setFullScreen() {
        fullScreen.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = binding.exoplayerView.getLayoutParams();
            if (!isFullScreen) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                binding.exoplayerView.setLayoutParams(params);

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                fullScreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_fullscreen_exit_24));
                isFullScreen = true;
            } else {
                params.height = 404;
                binding.exoplayerView.setLayoutParams(params);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                fullScreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_fullscreen));
                isFullScreen = false;
            }
        });
    }

    public void playSpeedFilm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
        builder.setTitle("Tốc độ phát");

        String[] items = {"0.5", "Chuẩn", "1.25", "1.5", "2"};

        builder.setSingleChoiceItems(items, markerSpeedPlayFilm, (dialog, which) -> {
            switch (which) {
                case 0 -> {
                    speed = 0.5f;
                    markerSpeedPlayFilm = 0;
                    tvSpeed.setText(R.string._0_5x);
                    tvSpeed.setVisibility(View.VISIBLE);
                }
                case 1 -> {
                    speed = 1f;
                    markerSpeedPlayFilm = 1;
                    tvSpeed.setVisibility(View.GONE);
                }
                case 2 -> {
                    speed = 1.25f;
                    markerSpeedPlayFilm = 2;
                    tvSpeed.setText(R.string._1_25x);
                    tvSpeed.setVisibility(View.VISIBLE);
                }
                case 3 -> {
                    speed = 1.5f;
                    markerSpeedPlayFilm = 3;
                    tvSpeed.setText(R.string._1_5x);
                    tvSpeed.setVisibility(View.VISIBLE);
                }
                case 4 -> {
                    speed = 2f;
                    markerSpeedPlayFilm = 4;
                    tvSpeed.setText(R.string._2x);
                    tvSpeed.setVisibility(View.VISIBLE);
                }
                default -> dialog.dismiss();
            }
            dialog.dismiss();
            player.setPlaybackSpeed(speed);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initialInfoRelate() {
        /* Khởi tạo viewpager và tabLayout */
        WatchFilmViewPagerAdapter viewPagerAdapter = new WatchFilmViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, ((tab, position) -> {
            switch (position) {
                case 0 -> tab.setText("Giới thiệu");
                case 1 -> tab.setText("Bình luận");
            }
        })).attach();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /* Lưu thời lượng phim đã xem */
        saveHistoryWatch();

        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
        }
    }

    private void saveHistoryWatch() {
        if (!idUser.equals("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            String dateWatch = simpleDateFormat.format(date);
            long timeWatch = player.getCurrentPosition();
            if (existHistory) {
                historyWatchFilm.setDayWatch(dateWatch);
                historyWatchFilm.setDuration(timeWatch);
                updateDataTimeWatchFilm(historyWatchFilm);
            } else {
                HistoryWatchFilm historyWatchFilm = new HistoryWatchFilm(film.getId(), timeWatch, dateWatch, film.getAvatar(), film.getName());
                collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_WATCHED).collection(idUser).add(historyWatchFilm);
            }
        }
    }

    private void updateDataTimeWatchFilm(HistoryWatchFilm history) {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_WATCHED).collection(idUser)
                .document(history.getDocumentID())
                .update("duration", history.getDuration(), "dayWatch", history.getDayWatch());
    }

    private void showDialog(String title) {
        new AlertDialog.Builder(this)
                .setMessage(title)
                .setPositiveButton("Xác nhận", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    public void loadTimeWatched() {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_WATCHED)
                .collection(idUser)
                .whereEqualTo(Constant.FirebaseFiretore.ID_FILM, film.getId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.getDocuments().size() > 0) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        historyWatchFilm = doc.toObject(HistoryWatchFilm.class);
                        if (historyWatchFilm != null) {
                            existHistory = true;
                            historyWatchFilm.setDocumentID(doc.getId());
                            player.seekTo(historyWatchFilm.getDuration());
                        }
                    }
                    if (auto_play) {
                        player.play();
                    }
                })
                .addOnFailureListener(task -> {
                    if (auto_play) {
                        player.play();
                    }
                });
    }
}