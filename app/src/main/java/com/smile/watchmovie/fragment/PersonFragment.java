package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.MainActivity;
import com.smile.watchmovie.adapter.FilmFavoriteAdapter;
import com.smile.watchmovie.adapter.HistoryWatchFilmAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentPersonBinding;
import com.smile.watchmovie.model.MovieDetailResponse;
import com.smile.watchmovie.model.MovieMainHome;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonFragment extends Fragment {

    private FragmentPersonBinding binding;
    private MainActivity mMainActivity;
    private CollectionReference collectionReferenceFilmFavorite;
    private String idUser;
    private List<MovieMainHome> mFilmFavoriteList;
    private FilmFavoriteAdapter filmFavoriteAdapter;
    private ProgressDialog progressDialog;
    private CollectionReference collectionReferenceHistory;
    private List<MovieMainHome> mHistoryList;
    private HistoryWatchFilmAdapter historyWatchFilmAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainActivity = (MainActivity) getActivity();
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        mFilmFavoriteList = new ArrayList<>();
        mHistoryList = new ArrayList<>();

        progressDialog = new ProgressDialog(mMainActivity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Load data...");
        progressDialog.show();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);

        binding.rcvHistoryWatchFilm.setLayoutManager(linearLayoutManager);
        binding.rcvHistoryWatchFilm.setFocusable(false);
        historyWatchFilmAdapter = new HistoryWatchFilmAdapter(mMainActivity);
        historyWatchFilmAdapter.setData(mHistoryList);
        binding.rcvHistoryWatchFilm.setAdapter(historyWatchFilmAdapter);

        binding.rcvFilmFavorite.setLayoutManager(linearLayoutManager1);
        binding.rcvFilmFavorite.setFocusable(false);
        filmFavoriteAdapter = new FilmFavoriteAdapter(mMainActivity);
        filmFavoriteAdapter.setData(mFilmFavoriteList);
        binding.rcvFilmFavorite.setAdapter(filmFavoriteAdapter);

        getIdUser();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceFilmFavorite = firebaseFirestore.collection("film_favorite_"+ idUser);
        collectionReferenceHistory = firebaseFirestore.collection("history_watch_film_"+ idUser);

        getFilmFavorite();
        getHistoryWatchFilm();

        return binding.getRoot();
    }

    private void getIdUser(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mMainActivity);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(acct == null && accessToken == null){
            //binding.ivNoFavorite.setVisibility(View.INVISIBLE);
        }
        else if(acct != null){
            this.idUser = acct.getId();
        }
        else if(!accessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    (object, response) -> {
                        // Application code
                        try {
                            assert object != null;
                            this.idUser = (String) object.get("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    public void getHistoryWatchFilm(){
        collectionReferenceHistory.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    int check = 0;
                    QuerySnapshot snapshot = task.getResult();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                        callApiGetHistoryWatchFilm(Integer.parseInt(idFilm1));
                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    private void callApiGetHistoryWatchFilm(int idFilm){
        ApiService.apiService.getFilmDetail("7da353b8a3246f851e0ee436d898a26d", idFilm).enqueue(new Callback<MovieDetailResponse>() {
            @SuppressLint({"StringFormatMatches", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<MovieDetailResponse> call, @NonNull Response<MovieDetailResponse> response) {
                MovieDetailResponse cinema = response.body();
                if(cinema != null) {
                    mHistoryList.add(cinema.getData());
                    historyWatchFilmAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(mMainActivity, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFilmFavorite(){
        collectionReferenceFilmFavorite.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    int check = 0;
                    QuerySnapshot snapshot = task.getResult();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                        callApiGetFilmFavorite(Integer.parseInt(idFilm1));
                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    private void callApiGetFilmFavorite(int idFilm){
        ApiService.apiService.getFilmDetail("7da353b8a3246f851e0ee436d898a26d", idFilm).enqueue(new Callback<MovieDetailResponse>() {
            @SuppressLint({"StringFormatMatches", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull Call<MovieDetailResponse> call, @NonNull Response<MovieDetailResponse> response) {
                MovieDetailResponse cinema = response.body();
                if(cinema != null) {
                    mFilmFavoriteList.add(cinema.getData());
                    filmFavoriteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(mMainActivity, "Error Get Film", Toast.LENGTH_SHORT).show();
            }
        });
    }
}