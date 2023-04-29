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
        setContentView(R.layout.activity_history_watch_film);

        binding = ActivityHistoryWatchFilmBinding.inflate(getLayoutInflater());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        historyFilmAdapter = new HistoryWatchFilmAdapter(this);
        historyFilmAdapter.setDeleteHistoryListener(this::openDeleteHistoryFilm);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvHistory.setLayoutManager(linearLayoutManager);
        binding.rcvHistory.addItemDecoration(itemDecoration);

        binding.rcvHistory.setAdapter(historyFilmAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceHistory = firebaseFirestore.collection("WatchFilm");

        binding.ivBack.setOnClickListener(v -> onBackPressed());


        setContentView(binding.getRoot());
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
        collectionReferenceHistory.document("tblhistorywatchfilm").collection(idUser).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                historyFilmAdapter.setData(snapshot);
                if(snapshot.size() > 0) {
                    binding.ivEmptyData.setVisibility(View.GONE);
                } else {
                    binding.ivEmptyData.setVisibility(View.VISIBLE);
                }
            }
            binding.progressLoadHistoryHome.setVisibility(View.INVISIBLE);
        });
   }

    private void deleteHistoryWatchFilm(HistoryWatchFilm historyWatchFilm) {
        collectionReferenceHistory.document("tblhistorywatchfilm")
                .collection(idUser)
                .document(historyWatchFilm.getDocumentID())
                .delete();
        getHistoryWatchFilm();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHistoryWatchFilm();
    }
}