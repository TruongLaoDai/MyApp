package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.adapter.EpisodeAdapter;
import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentIntroduceFilmBinding;
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
    private WatchFilmActivity mWatchFilmActivity;
    private FilmSearchAdapter mFilmSearchAdapter;
    private String idUser;
    private List<FilmMainHome> mFilmList;
    private FilmMainHome filmMainHome;
    private EpisodeAdapter mEpisodeAdapter;
    private boolean mIsLoading;
    private int mCurrentPage = 0;

    private DocumentReference documentReferenceFilmFavorite;
    private DocumentReference documentReferenceFilmLike;
    private DocumentReference documentReferenceFilmDislike;

    private int changeImageFavoriteFilm, changeImageDislikeFilm, changeImageLikeFilm;
    private int currentLike, currentDislike;
    private int statusLike, statusDislike, statusFavorite;
    private FilmReaction mediaLike, mediaDislike, mediaFavorite;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mWatchFilmActivity = (WatchFilmActivity) getActivity();
        binding = FragmentIntroduceFilmBinding.inflate(inflater, container, false);

        filmMainHome = new FilmMainHome();
        if (mWatchFilmActivity.filmMainHome != null) {
            setUpData();

            if (!mWatchFilmActivity.idUser.equals("")) {

                setUpFireBase();

                binding.loutFavorite.setOnClickListener(v -> setUpViewFavoriteFilm());

                binding.loutLike.setOnClickListener(v -> setUpViewLikeFilm());

                binding.loutDislike.setOnClickListener(v -> setUpViewDislikeFilm());

                binding.loutDownload.setOnClickListener(v -> Toast.makeText(mWatchFilmActivity, getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show());
            }

            playFilmFirst();
        }

        setUpView();

        return binding.getRoot();
    }

    private void setUpData() {
        filmMainHome = mWatchFilmActivity.filmMainHome;

        binding.tvNameFilm.setText(filmMainHome.getName());
        binding.tvViewNumber.setText(mWatchFilmActivity.getString(R.string.tv_view_number, filmMainHome.getViewNumber()));

        mEpisodeAdapter = new EpisodeAdapter(mWatchFilmActivity, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mWatchFilmActivity, RecyclerView.HORIZONTAL, false);
        binding.rcvEpisode.setLayoutManager(layoutManager);
    }

    private void setUpView() {
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

        binding.loutIntro.setOnClickListener(v -> clickOpenDetailFilm());

        callApiGetByCategoryListMovie(filmMainHome.getCategoryId(), 0);
    }

    private void clickOpenDetailFilm() {
        DetailFilmBottomSheetFragment myBottomSheetFragment = new DetailFilmBottomSheetFragment(filmMainHome);
        myBottomSheetFragment.show(mWatchFilmActivity.getSupportFragmentManager(), myBottomSheetFragment.getTag());
    }

    private void setUpFireBase() {
        idUser = mWatchFilmActivity.idUser;

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        documentReferenceFilmFavorite = firebaseFirestore.document("WatchFilm/tblfilmfavorite");
        documentReferenceFilmLike = firebaseFirestore.document("WatchFilm/tblfilmlike");
        documentReferenceFilmDislike = firebaseFirestore.document("WatchFilm/tblfilmdislike");

        isFilmFavorite();
        isFilmLike();
        isFilmDislike();
    }

    private void setUpViewFavoriteFilm() {
        if (changeImageFavoriteFilm == R.drawable.ic_add_favorite) {
            changeImageFavoriteFilm = R.drawable.ic_added_favorite;
            binding.tvFavorite.setTextColor(Color.parseColor("#2A48E8"));
            Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.add_film_favorite), Toast.LENGTH_LONG).show();
        } else if (changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            changeImageFavoriteFilm = R.drawable.ic_add_favorite;
            binding.tvFavorite.setTextColor(Color.parseColor("#777776"));
            Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.remove_film_favorite), Toast.LENGTH_LONG).show();
        }
        binding.ivAddFavorite.setImageResource(changeImageFavoriteFilm);
    }

    private void setUpViewLikeFilm() {
        if (changeImageLikeFilm == R.drawable.ic_like_film) {
            Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.add_film_like), Toast.LENGTH_LONG).show();
            if (changeImageDislikeFilm == R.drawable.ic_disliked) {
                currentDislike -= 1;
                if (currentDislike == 0) {
                    binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.dislike));
                } else {
                    binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentDislike));
                }
                changeImageDislikeFilm = R.drawable.ic_dislike;
                binding.ivDislike.setImageResource(changeImageDislikeFilm);
                binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"));
            }
            changeImageLikeFilm = R.drawable.ic_liked_film;
            binding.tvLikeNumber.setTextColor(Color.parseColor("#2A48E8"));
            currentLike += 1;
            binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentLike));
        } else if (changeImageLikeFilm == R.drawable.ic_liked_film) {
            Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.remove_film_like), Toast.LENGTH_LONG).show();
            changeImageLikeFilm = R.drawable.ic_like_film;
            binding.tvLikeNumber.setTextColor(Color.parseColor("#777776"));
            currentLike -= 1;
            if (currentLike == 0) {
                binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.like));
            } else {
                binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentLike));
            }
        }
        binding.ivLike.setImageResource(changeImageLikeFilm);
    }

    private void setUpViewDislikeFilm() {
        if (changeImageDislikeFilm == R.drawable.ic_dislike) {
            if (changeImageLikeFilm == R.drawable.ic_liked_film) {
                currentLike -= 1;
                if (currentLike == 0) {
                    binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.like));
                } else {
                    binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentLike));
                }
                changeImageLikeFilm = R.drawable.ic_like_film;
                binding.ivLike.setImageResource(changeImageLikeFilm);
                binding.tvLikeNumber.setTextColor(Color.parseColor("#777776"));
            }
            changeImageDislikeFilm = R.drawable.ic_disliked;
            binding.tvDislikeNumber.setTextColor(Color.parseColor("#2A48E8"));
            currentDislike += 1;
            binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentDislike));
            Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.add_film_dislike), Toast.LENGTH_LONG).show();
        } else if (changeImageDislikeFilm == R.drawable.ic_disliked) {
            binding.loutFavorite.setEnabled(true);
            changeImageDislikeFilm = R.drawable.ic_dislike;
            binding.tvDislikeNumber.setTextColor(Color.parseColor("#777776"));
            currentDislike -= 1;
            if (currentDislike == 0) {
                binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.dislike));
            } else {
                binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentDislike));
            }
            Toast.makeText(mWatchFilmActivity, mWatchFilmActivity.getString(R.string.remove_film_dislike), Toast.LENGTH_LONG).show();
        }
        binding.ivDislike.setImageResource(changeImageDislikeFilm);
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mFilmList.removeIf(filmMainHome1 -> filmMainHome1.getId() == filmMainHome.getId());
                        }
                    } else {
                        Toast.makeText(mWatchFilmActivity, "Đã hiển thị hết film", Toast.LENGTH_LONG).show();
                    }
                    if (page == 0) {
//                        mFilmSearchAdapter.setData(mFilmList);
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


    public void isFilmFavorite() {
        documentReferenceFilmFavorite
                .collection(idUser)
                .whereEqualTo("idFilm", filmMainHome.getId())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        this.mediaFavorite = queryDocumentSnapshots.getDocuments().get(0).toObject(FilmReaction.class);
                        if(mediaFavorite != null) {
                            statusFavorite = 2;
                            this.mediaFavorite.setDocumentId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            if(this.mediaFavorite.getType_reaction() == 1) {
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
                })
                .addOnFailureListener(e -> {
                    statusFavorite = 0;
                    changeImageFavoriteFilm = R.drawable.ic_add_favorite;
                    Toast.makeText(mWatchFilmActivity, "Get film favorite error", Toast.LENGTH_SHORT).show();
                });
    }

    public void isFilmLike() {
        documentReferenceFilmLike
                .collection(filmMainHome.getId() + "")
                .whereEqualTo("idUser", idUser)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        this.mediaLike = queryDocumentSnapshots.getDocuments().get(0).toObject(FilmReaction.class);
                        if(mediaLike != null) {
                            statusLike = 2;
                            this.mediaLike.setDocumentId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            if(this.mediaLike.getType_reaction() == 1) {
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
                })
                .addOnFailureListener(e -> {
                    statusLike = 0;
                    changeImageLikeFilm = R.drawable.ic_like_film;
                    Toast.makeText(mWatchFilmActivity, "Get film like error", Toast.LENGTH_SHORT).show();
                });

        documentReferenceFilmLike
                .collection(filmMainHome.getId() + "")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        currentLike = queryDocumentSnapshots.size();
                        if (currentLike > 0) {
                            binding.tvLikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentLike));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(mWatchFilmActivity, "Get film like error", Toast.LENGTH_SHORT).show());
    }

    public void isFilmDislike() {
        documentReferenceFilmDislike
                .collection(filmMainHome.getId() + "")
                .whereEqualTo("idUser", idUser)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        this.mediaDislike = queryDocumentSnapshots.getDocuments().get(0).toObject(FilmReaction.class);
                        if(mediaDislike != null) {
                            statusDislike = 2;
                            this.mediaDislike.setDocumentId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            if(this.mediaDislike.getType_reaction() == 1) {
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
                })
                .addOnFailureListener(e -> {
                    statusDislike = 0;
                    changeImageDislikeFilm = R.drawable.ic_dislike;
                    Toast.makeText(mWatchFilmActivity, "Get film dislike error", Toast.LENGTH_SHORT).show();
                });

        documentReferenceFilmDislike
                .collection(filmMainHome.getId() + "")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        currentDislike = queryDocumentSnapshots.size();
                        if (currentDislike > 0) {
                            binding.tvDislikeNumber.setText(mWatchFilmActivity.getString(R.string.number, currentDislike));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(mWatchFilmActivity, "Get film dislike error", Toast.LENGTH_SHORT).show());
    }

    private void updateFavorite() {
        if(statusFavorite == 1 && changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            documentReferenceFilmFavorite
                    .collection(idUser)
                    .document(this.mediaFavorite.getDocumentId())
                    .update("type_reaction", 1);
        } else if(statusFavorite == 2 && changeImageFavoriteFilm == R.drawable.ic_add_favorite) {
            documentReferenceFilmFavorite
                    .collection(idUser)
                    .document(this.mediaFavorite.getDocumentId())
                    .delete();
        } else if(statusFavorite ==0 && changeImageFavoriteFilm == R.drawable.ic_added_favorite) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            FilmReaction mediaReaction = new FilmReaction(filmMainHome.getId(), filmMainHome.getAvatar(), filmMainHome.getName(), format.format(new Date()), 1);
            documentReferenceFilmFavorite.collection(idUser).add(mediaReaction);
        }
    }

    private void updateFilmLike() {
        if(statusLike == 1 && changeImageLikeFilm == R.drawable.ic_liked_film) {
            documentReferenceFilmLike
                    .collection(filmMainHome.getId()+"")
                    .document(this.mediaLike.getDocumentId())
                    .update("type_reaction", 1);
        } else if(statusLike == 2 && changeImageLikeFilm == R.drawable.ic_like_film) {
            documentReferenceFilmLike
                    .collection(filmMainHome.getId()+"")
                    .document(this.mediaLike.getDocumentId())
                    .delete();
        } else if(statusLike ==0 && changeImageLikeFilm == R.drawable.ic_liked_film) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            FilmReaction mediaReaction = new FilmReaction(idUser, format.format(new Date()), 1);
            documentReferenceFilmLike.collection(filmMainHome.getId()+"").add(mediaReaction);
        }
    }

    private void updateFilmDisLike() {
        if(statusDislike == 1 && changeImageDislikeFilm == R.drawable.ic_disliked) {
            documentReferenceFilmDislike
                    .collection(filmMainHome.getId()+"")
                    .document(this.mediaDislike.getDocumentId())
                    .update("type_reaction", 1);
        } else if(statusDislike == 2 && changeImageDislikeFilm == R.drawable.ic_dislike) {
            documentReferenceFilmDislike
                    .collection(filmMainHome.getId()+"")
                    .document(this.mediaDislike.getDocumentId())
                    .delete();
        } else if(statusDislike ==0 && changeImageDislikeFilm == R.drawable.ic_disliked) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            FilmReaction mediaReaction = new FilmReaction(idUser, format.format(new Date()), 1);
            documentReferenceFilmDislike.collection(filmMainHome.getId()+"").add(mediaReaction);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateFilmLike();
        updateFavorite();
        updateFilmDisLike();
    }
}