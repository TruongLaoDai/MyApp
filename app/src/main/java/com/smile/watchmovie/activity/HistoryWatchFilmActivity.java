package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityHistoryWatchFilmBinding;
import com.smile.watchmovie.databinding.ActivityWatchFilmBinding;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.List;
import java.util.Objects;

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

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceHistory = firebaseFirestore.collection("history_watch_film_" + idUser);

        binding.ivBack.setOnClickListener(v -> finish());
        setContentView(binding.getRoot());

    }

    public void getHistoryWatchFilm() {
        collectionReferenceHistory.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int check = 0;
                QuerySnapshot snapshot = task.getResult();
                for (QueryDocumentSnapshot doc : snapshot) {
                    String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                    String time = Objects.requireNonNull(doc.get("time")).toString();
                    callApiGetHistoryWatchFilm(Integer.parseInt(idFilm1));
                    check = 1;
                }
                binding.progressLoadHistoryHome.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void callApiGetHistoryWatchFilm(int idFilm) {
        ApiService.apiService.getFilmDetail("7da353b8a3246f851e0ee436d898a26d", idFilm).enqueue(new Callback<FilmDetailResponse>() {
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