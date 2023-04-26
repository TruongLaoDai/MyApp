package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityHistoryWatchFilmBinding;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryWatchFilmActivity extends AppCompatActivity {

    private ActivityHistoryWatchFilmBinding binding;

    private CollectionReference collectionReferenceFilmFavorite;
    private String idUser;
    private CollectionReference collectionReferenceHistory;
    private List<FilmMainHome> mHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_watch_film);

        binding = ActivityHistoryWatchFilmBinding.inflate(getLayoutInflater());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        idUser = sharedPreferences.getString("idUser", "");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceHistory = firebaseFirestore.collection("watchfilm");

        binding.ivBack.setOnClickListener(v -> finish());
        setContentView(binding.getRoot());

    }

    public void getHistoryWatchFilm() {
        collectionReferenceHistory.document("tblhistorywatchfilm").collection("user" + idUser).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();

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
                if (cinema != null) {
                    mHistoryList.add(cinema.getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(HistoryWatchFilmActivity.this, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }
}