package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.adapter.EpisodeAdapter;
import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentIntroduceFilmBinding;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.SubFilm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IntroduceFilmFragment extends Fragment {

    private FragmentIntroduceFilmBinding binding;
    private WatchFilmActivity mWatchFilmActivity;
    private FilmSearchAdapter mFilmSearchAdapter;
    private String idUser;
    private List<FilmMainHome> mFilmList;
    private FilmMainHome filmMainHome;
    private EpisodeAdapter mEpisodeAdapter;
    private boolean mIsLoading;
    private int mCurrentPage = 0;

    private CollectionReference collectionReferenceFilmFavorite;
    private CollectionReference collectionReferenceFilmLike;
    private CollectionReference collectionReferenceFilmDislike;
    private CollectionReference collectionReferenceFilmDownloaded;

    private int changeImageFavoriteFilm;
    private int currentLike;
    private int changeImageLikeFilm;
    private int currentDislike;
    private int changeImageDislikeFilm;
    private int changeImageDownloadFilm;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mWatchFilmActivity = (WatchFilmActivity) getActivity();
        binding = FragmentIntroduceFilmBinding.inflate(inflater, container, false);

        filmMainHome = new FilmMainHome();
        if (mWatchFilmActivity.filmMainHome != null) {
            filmMainHome = mWatchFilmActivity.filmMainHome;

            binding.tvNameFilm.setText(filmMainHome.getName());
            binding.tvViewNumber.setText(mWatchFilmActivity.getString(R.string.tv_view_number, filmMainHome.getViewNumber()));

            mEpisodeAdapter = new EpisodeAdapter(mWatchFilmActivity, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mWatchFilmActivity, RecyclerView.HORIZONTAL, false);
            binding.rcvEpisode.setLayoutManager(layoutManager);

            if (!mWatchFilmActivity.idUser.equals("")) {
                idUser = mWatchFilmActivity.idUser;

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                collectionReferenceFilmFavorite = firebaseFirestore.collection("film_favorited_" + idUser);
                collectionReferenceFilmLike = firebaseFirestore.collection("film_liked_" + filmMainHome.getId());
                collectionReferenceFilmDislike = firebaseFirestore.collection("film_disliked_" + filmMainHome.getId());
                collectionReferenceFilmDislike = firebaseFirestore.collection("film_downloaded_" + filmMainHome.getId());

                isFilmFavorite();
                isFilmLike();
                isFilmDislike();

                binding.loutFavorite.setOnClickListener(v -> {
                    if (changeImageFavoriteFilm == R.drawable.ic_add_favorite) {
                        changeImageFavoriteFilm = R.drawable.ic_added_favorite;
                        binding.tvFavorite.setTextColor(Color.parseColor("#2A48E8"));
                        binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm);
                        addFilmFavorite();
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.add_film_favorite), Toast.LENGTH_LONG).show();
                    } else if (changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
                        changeImageFavoriteFilm = R.drawable.ic_add_favorite;
                        binding.tvFavorite.setTextColor(Color.parseColor("#777776"));
                        binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm);
                        deleteFilmFavorite();
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.remove_film_favorite), Toast.LENGTH_LONG).show();
                    }
                });

                binding.loutLike.setOnClickListener(v -> {
                    if (changeImageLikeFilm == R.drawable.ic_like_film) {
                        changeImageLikeFilm = R.drawable.ic_liked_film;
                        binding.tvLikeNumber.setTextColor(Color.parseColor("#2A48E8"));
                        currentLike += 1;
                        binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentLike));
                        addFilmLike();
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.add_film_favorite), Toast.LENGTH_LONG).show();
                    } else if (changeImageLikeFilm == R.drawable.ic_liked_film) {
                        changeImageLikeFilm = R.drawable.ic_like_film;
                        binding.tvLikeNumber.setTextColor(Color.parseColor("#777776"));
                        currentLike -= 1;
                        if(currentLike == 0){
                            binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.like));
                        }else {
                            binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentLike));
                        }
                        deleteFilmLike();
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.remove_film_favorite), Toast.LENGTH_LONG).show();
                    }
                    binding.ivLike.setImageResource(changeImageLikeFilm);
                });

                binding.loutDislike.setOnClickListener(v -> {
                    if (changeImageDislikeFilm == R.drawable.ic_dislike) {
                        changeImageDislikeFilm = R.drawable.ic_disliked;
                        binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"));
                        currentDislike += 1;
                        binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentDislike));
                        addFilmDislike();
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.add_film_like), Toast.LENGTH_LONG).show();
                    } else if (changeImageDislikeFilm == R.drawable.ic_disliked) {
                        changeImageDislikeFilm = R.drawable.ic_dislike;
                        binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"));
                        currentDislike -= 1;
                        if(currentDislike == 0){
                            binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.dislike));
                        }else {
                            binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentDislike));
                        }
                        deleteFilmDislike();
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.remove_film_like), Toast.LENGTH_LONG).show();
                    }
                    binding.ivDislike.setImageResource(changeImageDislikeFilm);
                });

                binding.loutDownload.setOnClickListener(v -> {
                    if (changeImageDownloadFilm == R.drawable.ic_download_film) {
                        changeImageDownloadFilm = R.drawable.ic_film_downloaded;
                        binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"));
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.downloading_film), Toast.LENGTH_LONG).show();
                    } else if (changeImageDownloadFilm == R.drawable.ic_film_downloaded) {
                        changeImageDownloadFilm = R.drawable.ic_download_film;
                        binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"));
                        Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.cancel_download_film), Toast.LENGTH_LONG).show();
                    }
                    binding.ivDislike.setImageResource(changeImageDownloadFilm);
                });
            }

            playFilmFirst();
        }

        mFilmList = new ArrayList<>();

        mFilmSearchAdapter = new FilmSearchAdapter(mWatchFilmActivity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mWatchFilmActivity, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mWatchFilmActivity, DividerItemDecoration.VERTICAL);
        binding.rcvMore.setLayoutManager(linearLayoutManager);
        binding.rcvMore.addItemDecoration(itemDecoration);

        binding.rcvMore.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoading) {
                    LinearLayoutManager linearLayoutManager1 = (LinearLayoutManager) binding.rcvMore.getLayoutManager();
                    if (linearLayoutManager1 != null && linearLayoutManager1.findLastCompletelyVisibleItemPosition() == mFilmList.size() - 1) {
                        mIsLoading = true;
                        binding.loadMore.setVisibility(View.VISIBLE);
                        mCurrentPage += 1;
                        loadNextPage(filmMainHome.getCategoryId());
                    }
                }
            }
        });

        callApiGetByCategoryListMovie(filmMainHome.getCategoryId(), 0);
        return binding.getRoot();
    }

    private void playFilmFirst() {
        if (filmMainHome.getEpisodesTotal() != 0) {
            if (filmMainHome.getSubVideoList() != null) {
                binding.loutEpisode.setVisibility(View.VISIBLE);
                Collections.sort(filmMainHome.getSubVideoList());
                mEpisodeAdapter.setData(filmMainHome.getSubVideoList());
            } else {
                List<SubFilm> subVideoList = new ArrayList<>();
                SubFilm subFilm = new SubFilm();

                subFilm.setEpisode(1);
                subFilm.setLink(filmMainHome.getLink());
                subVideoList.add(subFilm);
                filmMainHome.setSubVideoList(subVideoList);
                mEpisodeAdapter.setData(subVideoList);
            }
            binding.rcvEpisode.setAdapter(mEpisodeAdapter);
        } else {
            binding.loutEpisode.setVisibility(View.GONE);
        }
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
                    if (binding.loadIntro.getVisibility() == View.VISIBLE) {
                        binding.loadIntro.setVisibility(View.INVISIBLE);
                    }
                    if (binding.loadMore.getVisibility() == View.VISIBLE) {
                        binding.loadMore.setVisibility(View.INVISIBLE);
                    }
                    if (movieArrayResponse.getData() != null) {
                        mFilmList.addAll(movieArrayResponse.getData());
                    } else {
                        Toast.makeText(mWatchFilmActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                    }
                    if (page == 0) {
                        mFilmSearchAdapter.setData(mFilmList);
                        binding.rcvMore.setAdapter(mFilmSearchAdapter);
                    }
                    mFilmSearchAdapter.notifyDataSetChanged();
                    if (mFilmList.size() > 0 && movieArrayResponse.getData().size() == 0) {
                        Toast.makeText(mWatchFilmActivity, "", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mWatchFilmActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
                binding.loadMore.setVisibility(View.INVISIBLE);
                Toast.makeText(mWatchFilmActivity, "Error Get Film", Toast.LENGTH_SHORT).show();
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


    public void addFilmFavorite() {
        Map<String, Object> filmFavorite = new HashMap<>();
        filmFavorite.put("idFilm", filmMainHome.getId() + "");
        collectionReferenceFilmFavorite.add(filmFavorite);
    }

    public void isFilmFavorite() {
        collectionReferenceFilmFavorite.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int check = 0;
                QuerySnapshot snapshot = task.getResult();
                for (QueryDocumentSnapshot doc : snapshot) {
                    String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                    if (Integer.parseInt(idFilm1) == filmMainHome.getId()) {
                        check = 1;
                        changeImageFavoriteFilm = R.drawable.ic_added_favorite;
                        binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm);
                        binding.tvFavorite.setTextColor(Color.parseColor("#2A48E8"));
                    }
                }
                if (check == 0) {
                    changeImageFavoriteFilm = R.drawable.ic_add_favorite;
                }
            }
        });
    }

    private void deleteFilmFavorite() {
        collectionReferenceFilmFavorite.whereEqualTo("idFilm", filmMainHome.getId() + "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        collectionReferenceFilmFavorite.document(documentId)
                                .delete();
                    }
                });
    }

    public void addFilmLike() {
        Map<String, Object> filmLike = new HashMap<>();
        filmLike.put("idUser", idUser);
        collectionReferenceFilmLike.add(filmLike);
    }

    public void isFilmLike() {
        collectionReferenceFilmLike.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int check = 0;
                QuerySnapshot snapshot = task.getResult();
                currentLike = snapshot.size();
                for (QueryDocumentSnapshot doc : snapshot) {
                    String idFilm1 = Objects.requireNonNull(doc.get("idUser")).toString();
                    if (Integer.parseInt(idFilm1) == filmMainHome.getId()) {
                        check = 1;
                        changeImageLikeFilm = R.drawable.ic_liked_film;
                        binding.ivLike.setImageResource(changeImageLikeFilm);
                        binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentLike));
                        binding.tvLikeNumber.setTextColor(Color.parseColor("#2A48E8"));
                    }
                }
                if (check == 0) {
                    changeImageLikeFilm = R.drawable.ic_like_film;
                }
            }
        });
    }

    private void deleteFilmLike() {
        collectionReferenceFilmLike.whereEqualTo("idUser", idUser + "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        collectionReferenceFilmLike.document(documentId)
                                .delete();
                    }
                });
    }

    public void addFilmDislike() {
        Map<String, Object> filmDislike = new HashMap<>();
        filmDislike.put("idUser", idUser);
        collectionReferenceFilmDislike.add(filmDislike);
    }

    public void isFilmDislike() {
        collectionReferenceFilmDislike.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int check = 0;
                QuerySnapshot snapshot = task.getResult();
                currentDislike = snapshot.size();
                for (QueryDocumentSnapshot doc : snapshot) {
                    String idFilm1 = Objects.requireNonNull(doc.get("idUser")).toString();
                    if (Integer.parseInt(idFilm1) == filmMainHome.getId()) {
                        check = 1;
                        changeImageDislikeFilm = R.drawable.ic_disliked;
                        binding.ivDislike.setImageResource(changeImageLikeFilm);
                        binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentDislike));
                        binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"));
                    }
                }
                if (check == 0) {
                    changeImageDislikeFilm = R.drawable.ic_dislike;
                }
            }
        });
    }

    private void deleteFilmDislike() {
        collectionReferenceFilmDislike.whereEqualTo("idUser", idUser + "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        collectionReferenceFilmDislike.document(documentId)
                                .delete();
                    }
                });
    }
}