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
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

public class WatchFilmActivity extends AppCompatActivity implements Player.Listener {

    private ActivityWatchFilmBinding binding;
    public FilmMainHome film;
    private ExoPlayer player;
    private ImageView fullScreen;
    private TextView tvSpeed;
    private CollectionReference collectionReference;
    public String idUser, isVip;
    private boolean isFullScreen = false, existHistory = false;
    private float speed;
    private PlaybackParameters playbackParameters;
    private boolean auto_play, full_screen;
    private HistoryWatchFilm historyWatchFilm;

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

            /* Load lịch sử xem */
            loadTimeWatched();
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
        MediaItem mediaItem = MediaItem.fromUri(subFilm.getLink());
        player.setMediaItem(mediaItem);
        player.prepare();
        if (auto_play) {
            player.play();
        }
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
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(WatchFilmActivity.this);
        alBuilder.setTitle("Tốc độ phát");

        String[] items = {"0.5", "Chuẩn", "1.25", "1.5", "2"};
        int checkedItem = -1;

        alBuilder.setSingleChoiceItems(items, checkedItem, (dialog, which) -> {
            switch (which) {
                case 0 -> {
                    speed = 0.5f;
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("0.5x");
                    dialog.dismiss();
                }
                case 1 -> {
                    speed = 1f;
                    tvSpeed.setVisibility(View.GONE);
                    dialog.dismiss();
                }
                case 2 -> {
                    speed = 1.25f;
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("1.25x");
                    dialog.dismiss();
                }
                case 3 -> {
                    speed = 1.5f;
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("1.5x");
                    dialog.dismiss();
                }
                case 4 -> {
                    speed = 2f;
                    tvSpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setText("2x");
                    dialog.dismiss();
                }
                default -> dialog.dismiss();
            }
            playbackParameters = new PlaybackParameters(speed);
            player.setPlaybackParameters(playbackParameters);
        });

        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    private void initialInfoRelate() {
        /* Khởi tạo viewpager và tabLayout */
        WatchFilmViewPagerAdapter viewPagerAdapter = new WatchFilmViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);
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
//        saveHistoryWatch();
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
        }
    }

    private void saveHistoryWatch() {
        if (!idUser.equals("") && player.getCurrentPosition() >= 30000) {
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
                collectionReference.document("tblhistorywatchfilm").collection(idUser).add(historyWatchFilm);
            }
        }
    }

    private void updateDataTimeWatchFilm(HistoryWatchFilm history) {
        collectionReference.document("tblhistorywatchfilm").collection(idUser)
                .document(history.getDocumentID())
                .update("duration", history.getDuration(), "dayWatch", history.getDayWatch());
    }

    private String messagePlayAtTime(long time) {
        int hour = (int) (time / 1000 / 3600);
        int minute = (int) ((time / 1000 - hour * 3600) / 60);
        int second = (int) (time / 1000 - hour * 3600 - minute * 60);
        return "Bạn có muốn tiếp tục xem bộ phim tại thời điểm: " + hour + ":" + minute + ":" + second + " hay không?";
    }

    private void showDialog(String title) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(title)
                .setPositiveButton("Xác nhận", (dialog, id) -> dialog.dismiss())
                .show();
    }

    public void loadTimeWatched() {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_WATCHED)
                .collection(idUser)
                .whereEqualTo(Constant.FirebaseFiretore.ID_FILM, film.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        historyWatchFilm = doc.toObject(HistoryWatchFilm.class);
                        if (historyWatchFilm != null) {
                            historyWatchFilm.setDocumentID(doc.getId());
                            /* showDialog(messagePlayAtTime(historyWatchFilm.getDuration()), 2, historyWatchFilm.getDuration()); */
                        }
                    }
                });
    }
}