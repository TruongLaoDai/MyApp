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
import com.smile.watchmovie.adapter.HistoryWatchFilmAdapter;
import com.smile.watchmovie.databinding.ActivityHistoryWatchFilmBinding;
import com.smile.watchmovie.fragment.DeleteBottomSheetFragment;
import com.smile.watchmovie.model.HistoryWatchFilm;

public class HistoryWatchFilmActivity extends AppCompatActivity {

    private ActivityHistoryWatchFilmBinding binding;
    private String idUser;
    private CollectionReference collectionReferenceHistory;
    private HistoryWatchFilmAdapter historyFilmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryWatchFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        historyFilmAdapter = new HistoryWatchFilmAdapter(this);
        historyFilmAdapter.setDeleteHistoryListener(this::openDeleteHistoryFilm);

        binding.rcvHistory.setAdapter(historyFilmAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceHistory = firebaseFirestore.collection("WatchFilm");

        binding.toolBar.setNavigationOnClickListener(view -> finish());

        getHistoryWatchFilm();
    }

    private void openDeleteHistoryFilm(HistoryWatchFilm historyWatchFilm) {
        DeleteBottomSheetFragment deleteBottomSheetFragment = new DeleteBottomSheetFragment();
        deleteBottomSheetFragment.setHistoryWatchFilm(historyWatchFilm);
        deleteBottomSheetFragment.setDeleteHistoryListener(this::deleteHistoryWatchFilm);
        deleteBottomSheetFragment.show(getSupportFragmentManager(), deleteBottomSheetFragment.getTag());
    }

    @SuppressLint({"StringFormatMatches", "NotifyDataSetChanged"})
    public void getHistoryWatchFilm() {
        binding.progressLoadHistoryHome.setVisibility(View.VISIBLE);
        collectionReferenceHistory.document("tblhistorywatchfilm").collection(idUser).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        binding.progressLoadHistoryHome.setVisibility(View.GONE);
                        if (!snapshot.isEmpty()) {
                            historyFilmAdapter.setData(snapshot);
                        } else {
                            binding.rcvHistory.setVisibility(View.GONE);
                            binding.tvContent.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void deleteHistoryWatchFilm(HistoryWatchFilm historyWatchFilm) {
        collectionReferenceHistory.document("tblhistorywatchfilm")
                .collection(idUser)
                .document(historyWatchFilm.getDocumentID())
                .delete();
        getHistoryWatchFilm();
    }
}