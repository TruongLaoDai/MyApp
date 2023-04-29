package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.FavoriteFilmAdapter;
import com.smile.watchmovie.databinding.ActivityFavoriteFilmBinding;
import com.smile.watchmovie.fragment.DeleteBottomSheetFragment;
import com.smile.watchmovie.model.FilmReaction;
import com.smile.watchmovie.model.HistoryWatchFilm;

public class FavoriteFilmActivity extends AppCompatActivity {
    private ActivityFavoriteFilmBinding binding;
    private String idUser;
    private CollectionReference collectionReferenceFavorite;
    private FavoriteFilmAdapter favoriteFilmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_film);

        binding = ActivityFavoriteFilmBinding.inflate(getLayoutInflater());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        favoriteFilmAdapter = new FavoriteFilmAdapter(this);
        favoriteFilmAdapter.setUnFavoriteListener(this::openUnFavoriteFilm);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvFavorite.setLayoutManager(linearLayoutManager);
        binding.rcvFavorite.addItemDecoration(itemDecoration);

        binding.rcvFavorite.setAdapter(favoriteFilmAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceFavorite = firebaseFirestore.collection("WatchFilm");

        binding.ivBack.setOnClickListener(v -> onBackPressed());


        setContentView(binding.getRoot());
        getFilmFavorite();
    }
    @SuppressLint({"StringFormatMatches", "NotifyDataSetChanged"})
    public void getFilmFavorite() {
        collectionReferenceFavorite.document("tblfilmfavorite").collection(idUser).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        favoriteFilmAdapter.setData(snapshot);
                        if(snapshot.size() > 0) {
                            binding.ivEmptyData.setVisibility(View.GONE);
                        } else {
                            binding.ivEmptyData.setVisibility(View.VISIBLE);
                        }
                    }
                    binding.progressLoadFavoriteHome.setVisibility(View.INVISIBLE);
                });
    }

    private void openUnFavoriteFilm(FilmReaction favoriteFilm) {
        DeleteBottomSheetFragment deleteBottomSheetFragment = new DeleteBottomSheetFragment();
        deleteBottomSheetFragment.setFavoriteFilm(favoriteFilm);
        deleteBottomSheetFragment.setUnFavoriteFilmListener(this::unFavoriteFilm);
        deleteBottomSheetFragment.show(getSupportFragmentManager(), deleteBottomSheetFragment.getTag());
    }

    private void unFavoriteFilm(FilmReaction filmFavorite) {
        collectionReferenceFavorite.document("tblfilmfavorite")
                .collection(idUser)
                .document(filmFavorite.getDocumentId())
                .delete();
        getFilmFavorite();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFilmFavorite();
    }
}