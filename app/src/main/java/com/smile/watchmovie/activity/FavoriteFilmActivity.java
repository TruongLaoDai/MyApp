package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.adapter.FavoriteFilmAdapter;
import com.smile.watchmovie.databinding.ActivityFavoriteFilmBinding;
import com.smile.watchmovie.fragment.DeleteBottomSheetFragment;
import com.smile.watchmovie.model.FilmReaction;

public class FavoriteFilmActivity extends AppCompatActivity {
    private ActivityFavoriteFilmBinding binding;
    private String idUser;
    private CollectionReference collectionReferenceFavorite;
    private FavoriteFilmAdapter favoriteFilmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        favoriteFilmAdapter = new FavoriteFilmAdapter(this);
        favoriteFilmAdapter.setUnFavoriteListener(this::openUnFavoriteFilm);

        binding.rcvFavorite.setAdapter(favoriteFilmAdapter);
        binding.toolBar.setNavigationOnClickListener(view -> finish());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceFavorite = firebaseFirestore.collection("WatchFilm");

        getFilmFavorite();
    }

    @SuppressLint({"StringFormatMatches", "NotifyDataSetChanged"})
    public void getFilmFavorite() {
        binding.progressLoadFavoriteHome.setVisibility(View.VISIBLE);
        collectionReferenceFavorite.document("tblfilmfavorite").collection(idUser).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        binding.progressLoadFavoriteHome.setVisibility(View.GONE);
                        if (!snapshot.isEmpty()) {
                            favoriteFilmAdapter.setData(snapshot);
                        } else {
                            binding.rcvFavorite.setVisibility(View.GONE);
                            binding.tvContent.setVisibility(View.VISIBLE);
                        }
                    }
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