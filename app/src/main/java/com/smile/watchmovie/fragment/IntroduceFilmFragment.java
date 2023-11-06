package com.smile.watchmovie.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.adapter.EpisodeAdapter;
import com.smile.watchmovie.adapter.FilmRelativeAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentIntroduceFilmBinding;
import com.smile.watchmovie.dialog.InfoFilmDialog;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.FilmReaction;
import com.smile.watchmovie.model.SubFilm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroduceFilmFragment extends Fragment {
    private FragmentIntroduceFilmBinding binding;
    private WatchFilmActivity activity;
    private FilmRelativeAdapter relateMovieListAdapter;
    private String idUser;
    private FilmMainHome filmMainHome;
    private EpisodeAdapter mEpisodeAdapter;
    private DocumentReference documentReferenceFilmFavorite, documentReferenceFilmLike, documentReferenceFilmDislike;
    private int changeImageFavoriteFilm, changeImageDislikeFilm, changeImageLikeFilm;
    private int currentLike, currentDislike;
    private int statusLike, statusDislike, statusFavorite;
    private FilmReaction mediaLike, mediaDislike, mediaFavorite;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIntroduceFilmBinding.inflate(inflater, container, false);

        activity = (WatchFilmActivity) getActivity();

        if (activity != null && activity.film != null) {
            initializeData();
            if (!activity.idUser.equals("")) {
                setUpFireBase();
            }
            playFilmFirst();
            handleEventClick();
        }

        return binding.getRoot();
    }

    private void initialFilmRelate() {
        callApiGetByCategoryListMovie(filmMainHome.getCategoryId(), 1);
    }

    private void handleEventClick() {
        /* Bắt sự kiện người dùng nhấn xem mô tả phim */
        binding.loutIntro.setOnClickListener(v -> clickOpenDetailFilm());

        /* Nhấn like */
        binding.loutLike.setOnClickListener(v -> {
            if (idUser == null || idUser.equals("")) {
                Toast.makeText(requireActivity(), "Bạn cần đăng nhập tài khoản để thực hiện tính năng này", Toast.LENGTH_SHORT).show();
            } else {
                setUpViewLikeFilm();
            }
        });

        /* Nhấn dislike */
        binding.loutDislike.setOnClickListener(v -> {
            if (idUser == null || idUser.equals("")) {
                Toast.makeText(requireActivity(), "Bạn cần đăng nhập tài khoản để thực hiện tính năng này", Toast.LENGTH_SHORT).show();
            } else {
                setUpViewDislikeFilm();
            }
        });

        /* Nhấn tải phim */
        binding.loutDownload.setOnClickListener(v ->
                Toast.makeText(activity, getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );

        /* Nhấn yêu thích */
        binding.loutFavorite.setOnClickListener(v -> {
            if (idUser == null || idUser.equals("")) {
                Toast.makeText(requireActivity(), "Bạn cần đăng nhập tài khoản để thực hiện tính năng này", Toast.LENGTH_SHORT).show();
            } else {
                setUpViewFavoriteFilm();
            }
        });
    }

    private void initializeData() {
        filmMainHome = activity.film;

        relateMovieListAdapter = new FilmRelativeAdapter(requireActivity());
        mEpisodeAdapter = new EpisodeAdapter(requireActivity());

        binding.rcvMore.setAdapter(relateMovieListAdapter);
        binding.rcvMore.setHasFixedSize(true);

        binding.rcvEpisode.setAdapter(mEpisodeAdapter);
        binding.rcvEpisode.setHasFixedSize(true);

        /* Hiển thị thông tin cơ bản về phim */
        binding.tvNameFilm.setText(filmMainHome.getName());
        binding.tvViewNumber.setText(activity.getString(R.string.tv_view_number, filmMainHome.getViewNumber()));

        /* Hiển thị các bộ phim liên quan */
        initialFilmRelate();

        /* Hiển thị số tập phim của phim đang chiếu */
        showEpisodesTotal();
    }

    private void showEpisodesTotal() {
        if (filmMainHome.getEpisodesTotal() != 0) {
            if (filmMainHome.getSubVideoList() != null) {
                binding.loutEpisode.setVisibility(View.VISIBLE);
                Collections.sort(filmMainHome.getSubVideoList());
                mEpisodeAdapter.setData(filmMainHome.getSubVideoList());
            } else {
                binding.loutEpisode.setVisibility(View.GONE);
            }
        } else {
            binding.loutEpisode.setVisibility(View.GONE);
        }
    }

    private void clickOpenDetailFilm() {
        InfoFilmDialog dialog = new InfoFilmDialog(filmMainHome, requireActivity());
        dialog.show(activity.getSupportFragmentManager(), InfoFilmDialog.TAG);
    }

    private void setUpFireBase() {
        idUser = activity.idUser;

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        documentReferenceFilmFavorite = firebaseFirestore.document("WatchFilm/tblfilmfavorite");
        documentReferenceFilmLike = firebaseFirestore.document("WatchFilm/tblfilmlike");
        documentReferenceFilmDislike = firebaseFirestore.document("WatchFilm/tblfilmdislike");

        loadFavorite();
        loadLike();
        loadDislike();
    }

    private void setUpViewFavoriteFilm() {
        if (changeImageFavoriteFilm == R.drawable.ic_add_favorite) {
            changeImageFavoriteFilm = R.drawable.ic_added_favorite;
            binding.tvFavorite.setTextColor(Color.parseColor("#2A48E8"));
            Toast.makeText(activity, activity.getString(R.string.add_film_favorite), Toast.LENGTH_LONG).show();
        } else if (changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            changeImageFavoriteFilm = R.drawable.ic_add_favorite;
            binding.tvFavorite.setTextColor(Color.parseColor("#777776"));
            Toast.makeText(activity, activity.getString(R.string.remove_film_favorite), Toast.LENGTH_LONG).show();
        }
        binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm);
        updateFavorite();
    }

    private void setUpViewLikeFilm() {
        if (changeImageLikeFilm == R.drawable.ic_like_film) {
            if (changeImageDislikeFilm == R.drawable.ic_disliked) {
                currentDislike -= 1;
                if (currentDislike < 0) {
                    currentDislike = 0;
                }

                binding.tvDislikeNumber.setText(activity.getString(R.string.dislike));
                changeImageDislikeFilm = R.drawable.ic_dislike;
                binding.ivDislike.setImageResource(changeImageDislikeFilm);
                binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"));
            }

            changeImageLikeFilm = R.drawable.ic_liked_film;
            binding.tvLikeNumber.setTextColor(Color.parseColor("#2A48E8"));
            currentLike += 1;
            binding.tvLikeNumber.setText(activity.getString(R.string.number, currentLike));
        } else if (changeImageLikeFilm == R.drawable.ic_liked_film) {
            changeImageLikeFilm = R.drawable.ic_like_film;
            binding.tvLikeNumber.setTextColor(Color.parseColor("#777776"));
            currentLike -= 1;
            if (currentLike < 0) {
                currentLike = 0;
            }

            binding.tvLikeNumber.setText(activity.getString(R.string.like));
        }
        binding.ivLike.setImageResource(changeImageLikeFilm);
        updateFilmLike();
        updateFilmDisLike();
    }

    private void setUpViewDislikeFilm() {
        if (changeImageDislikeFilm == R.drawable.ic_dislike) {
            if (changeImageLikeFilm == R.drawable.ic_liked_film) {
                currentLike -= 1;
                if (currentLike < 0) {
                    currentLike = 0;
                }

                binding.tvLikeNumber.setText(activity.getString(R.string.like));
                changeImageLikeFilm = R.drawable.ic_like_film;
                binding.ivLike.setImageResource(changeImageLikeFilm);
                binding.tvLikeNumber.setTextColor(Color.parseColor("#777776"));
            }
            changeImageDislikeFilm = R.drawable.ic_disliked;
            binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"));
            currentDislike += 1;
            binding.tvDislikeNumber.setText(activity.getString(R.string.number, currentDislike));
        } else if (changeImageDislikeFilm == R.drawable.ic_disliked) {
            changeImageDislikeFilm = R.drawable.ic_dislike;
            binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"));
            currentDislike -= 1;
            if (currentDislike < 0) {
                currentDislike = 0;
            }
            binding.tvDislikeNumber.setText(activity.getString(R.string.dislike));
        }
        binding.ivDislike.setImageResource(changeImageDislikeFilm);
        updateFilmDisLike();
        updateFilmLike();
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
        ApiService.apiService.getFilmByCategory("7da353b8a3246f851e0ee436d898a26d", categoryId, page, 15).enqueue(new Callback<FilmArrayResponse>() {
            @Override
            public void onResponse(@NonNull Call<FilmArrayResponse> call, @NonNull Response<FilmArrayResponse> response) {
                if (response.body() != null && response.body().getData() != null) {
                    relateMovieListAdapter.updateData((ArrayList<FilmMainHome>) response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmArrayResponse> call, @NonNull Throwable t) {
            }
        });
    }

    public void loadFavorite() {
        documentReferenceFilmFavorite
                .collection(idUser)
                .whereEqualTo("idFilm", filmMainHome.getId())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        this.mediaFavorite = queryDocumentSnapshots.getDocuments().get(0).toObject(FilmReaction.class);
                        if (mediaFavorite != null) {
                            this.mediaFavorite.setDocumentId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            if (this.mediaFavorite.getType_reaction() == 1) {
                                statusFavorite = 2;
                                changeImageFavoriteFilm = R.drawable.ic_added_favorite;
                                binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm);
                                binding.tvFavorite.setTextColor(Color.parseColor("#2A48E8"));
                            } else {
                                changeImageFavoriteFilm = R.drawable.ic_add_favorite;
                                statusFavorite = 1;
                            }
                        } else {
                            statusFavorite = 0;
                        }
                    } else {
                        changeImageFavoriteFilm = R.drawable.ic_add_favorite;
                        statusFavorite = 0;
                    }
                });
    }

    public void loadLike() {
        documentReferenceFilmLike
                .collection(filmMainHome.getId() + "")
                .whereEqualTo("idUser", idUser)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        mediaLike = queryDocumentSnapshots.getDocuments().get(0).toObject(FilmReaction.class);
                        if (mediaLike != null) {
                            mediaLike.setDocumentId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            if (mediaLike.getType_reaction() == 1) {
                                statusLike = 2;
                                changeImageDislikeFilm = R.drawable.ic_dislike;
                                changeImageLikeFilm = R.drawable.ic_liked_film;
                                binding.ivLike.setImageResource(changeImageLikeFilm);
                                binding.tvLikeNumber.setTextColor(Color.parseColor("#2A48E8"));
                            } else {
                                changeImageLikeFilm = R.drawable.ic_like_film;
                                statusLike = 1;
                            }
                        } else {
                            statusLike = 0;
                        }
                    } else {
                        changeImageLikeFilm = R.drawable.ic_like_film;
                        statusLike = 0;
                    }
                });
    }

    public void loadDislike() {
        documentReferenceFilmDislike
                .collection(filmMainHome.getId() + "")
                .whereEqualTo("idUser", idUser)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        this.mediaDislike = queryDocumentSnapshots.getDocuments().get(0).toObject(FilmReaction.class);
                        if (mediaDislike != null) {
                            mediaDislike.setDocumentId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            if (mediaDislike.getType_reaction() == 1) {
                                statusDislike = 2;
                                changeImageDislikeFilm = R.drawable.ic_disliked;
                                changeImageLikeFilm = R.drawable.ic_like_film;
                                binding.ivDislike.setImageResource(changeImageDislikeFilm);
                                binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"));
                            } else {
                                changeImageDislikeFilm = R.drawable.ic_dislike;
                                statusDislike = 1;
                            }
                        } else {
                            statusDislike = 0;
                        }
                    } else {
                        changeImageDislikeFilm = R.drawable.ic_dislike;
                        statusDislike = 0;
                    }
                });
    }

    private void updateFavorite() {
        if (statusFavorite == 1 && changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            documentReferenceFilmFavorite
                    .collection(idUser)
                    .document(this.mediaFavorite.getDocumentId())
                    .update("type_reaction", 1);
        } else if (statusFavorite == 2 && changeImageFavoriteFilm == R.drawable.ic_add_favorite) {
            documentReferenceFilmFavorite
                    .collection(idUser)
                    .document(this.mediaFavorite.getDocumentId())
                    .delete();
        } else if (statusFavorite == 0 && changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            FilmReaction mediaReaction = new FilmReaction(filmMainHome.getId(), filmMainHome.getAvatar(), filmMainHome.getName(), format.format(new Date()), 1);
            documentReferenceFilmFavorite.collection(idUser).add(mediaReaction);
        }
    }

    private void updateFilmLike() {
        if (statusLike == 1 && changeImageLikeFilm == R.drawable.ic_liked_film) {
            documentReferenceFilmLike
                    .collection(filmMainHome.getId() + "")
                    .document(this.mediaLike.getDocumentId())
                    .update("type_reaction", 1);
        } else if (statusLike == 2 && changeImageLikeFilm == R.drawable.ic_like_film) {
            documentReferenceFilmLike
                    .collection(filmMainHome.getId() + "")
                    .document(this.mediaLike.getDocumentId())
                    .delete();
        } else if (statusLike == 0 && changeImageLikeFilm == R.drawable.ic_liked_film) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            FilmReaction mediaReaction = new FilmReaction(idUser, format.format(new Date()), 1);
            documentReferenceFilmLike.collection(filmMainHome.getId() + "").add(mediaReaction);
        }
    }

    private void updateFilmDisLike() {
        if (statusDislike == 1 && changeImageDislikeFilm == R.drawable.ic_disliked) {
            documentReferenceFilmDislike
                    .collection(filmMainHome.getId() + "")
                    .document(this.mediaDislike.getDocumentId())
                    .update("type_reaction", 1);
        } else if (statusDislike == 2 && changeImageDislikeFilm == R.drawable.ic_dislike) {
            documentReferenceFilmDislike
                    .collection(filmMainHome.getId() + "")
                    .document(this.mediaDislike.getDocumentId())
                    .delete();
        } else if (statusDislike == 0 && changeImageDislikeFilm == R.drawable.ic_disliked) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            FilmReaction mediaReaction = new FilmReaction(idUser, format.format(new Date()), 1);
            documentReferenceFilmDislike.collection(filmMainHome.getId() + "").add(mediaReaction);
        }
    }
}