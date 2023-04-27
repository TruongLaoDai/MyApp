package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.FilmSearchAdapter;
import com.smile.watchmovie.adapter.HistoryUpVipAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityHistoryWatchFilmBinding;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryWatchFilmActivity extends AppCompatActivity {

    private ActivityHistoryWatchFilmBinding binding;

    private CollectionReference collectionReferenceFilmFavorite;
    private String idUser;
    private CollectionReference collectionReferenceHistory;
    private List<FilmMainHome> historyWatchFilm;
    private FilmSearchAdapter historyFilmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_watch_film);

        binding = ActivityHistoryWatchFilmBinding.inflate(getLayoutInflater());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        historyWatchFilm = new ArrayList<>();
        historyFilmAdapter = new FilmSearchAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvHistory.setLayoutManager(linearLayoutManager);
        binding.rcvHistory.addItemDecoration(itemDecoration);

        historyFilmAdapter.setData(historyWatchFilm);
        binding.rcvHistory.setAdapter(historyFilmAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceHistory = firebaseFirestore.collection("WatchFilm");

        binding.ivBack.setOnClickListener(v -> finish());


        setContentView(binding.getRoot());

    }

    public void getHistoryWatchFilm() {
        collectionReferenceHistory.document("tblhistorywatchfilm").collection("user" + idUser).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                for(DocumentSnapshot doc: snapshot) {

                }
                binding.progressLoadHistoryHome.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void callApiGetHistoryWatchFilm(int idFilm) {
        ApiService.apiService.getFilmDetail(getString(R.string.wsToken), idFilm).enqueue(new Callback<FilmDetailResponse>() {
            @SuppressLint({"StringFormatMatches", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<FilmDetailResponse> call, @NonNull Response<FilmDetailResponse> response) {
                FilmDetailResponse cinema = response.body();
                binding.progressLoadHistoryHome.setVisibility(View.GONE);
                if (cinema != null) {
                    historyWatchFilm.add(cinema.getData());
                    historyFilmAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmDetailResponse> call, @NonNull Throwable t) {
                binding.progressLoadHistoryHome.setVisibility(View.GONE);
                Toast.makeText(HistoryWatchFilmActivity.this, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }
}