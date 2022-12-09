package com.smile.watchmovie.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.LoginActivity;
import com.smile.watchmovie.MainActivity;
import com.smile.watchmovie.R;
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
    private CollectionReference collectionReferenceHistory;
    private List<MovieMainHome> mHistoryList;
    private HistoryWatchFilmAdapter historyWatchFilmAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainActivity = (MainActivity) getActivity();
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        mFilmFavoriteList = new ArrayList<>();
        mHistoryList = new ArrayList<>();

        getIdUser();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferenceFilmFavorite = firebaseFirestore.collection("film_favorite_"+ idUser);
        collectionReferenceHistory = firebaseFirestore.collection("history_watch_film_"+ idUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mMainActivity, RecyclerView.HORIZONTAL, false);

        binding.rcvHistoryWatchFilm.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(mMainActivity, DividerItemDecoration.HORIZONTAL);
        binding.rcvHistoryWatchFilm.addItemDecoration(itemDecoration);
        historyWatchFilmAdapter = new HistoryWatchFilmAdapter(mMainActivity, this::clickDeleteHistory);
        historyWatchFilmAdapter.setData(mHistoryList);
        binding.rcvHistoryWatchFilm.setAdapter(historyWatchFilmAdapter);

        binding.rcvFilmFavorite.setLayoutManager(linearLayoutManager1);
        binding.rcvFilmFavorite.setFocusable(false);
        RecyclerView.ItemDecoration itemDecoration1=new DividerItemDecoration(mMainActivity, DividerItemDecoration.HORIZONTAL);
        binding.rcvFilmFavorite.addItemDecoration(itemDecoration1);
        filmFavoriteAdapter = new FilmFavoriteAdapter(mMainActivity, this::clickUnFavoriteFilm);
        filmFavoriteAdapter.setData(mFilmFavoriteList);
        binding.rcvFilmFavorite.setAdapter(filmFavoriteAdapter);

        getFilmFavorite();
        getHistoryWatchFilm();

        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void clickDeleteHistory(MovieMainHome movieMainHome){
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_watch_film_from_time);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        TextView tv_title = dialog.findViewById(R.id.tv_at_time);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);

        tv_title.setText("Bạn có muốn xóa film " + movieMainHome.getName() + " khỏi lịch sử xem phim của bạn?");


        btn_yes.setOnClickListener(v -> {
            collectionReferenceHistory.whereEqualTo("idFilm", movieMainHome.getId() +"")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            collectionReferenceHistory.document(documentId)
                                    .delete();
                        }
                    });
            mHistoryList.remove(movieMainHome);
            if(mHistoryList.size() == 0){
                binding.tvSizeHistory.setVisibility(View.VISIBLE);
            }
            historyWatchFilmAdapter.notifyDataSetChanged();
            dialog.dismiss();
            Toast.makeText(mMainActivity, "Bạn đã xóa film thành công",Toast.LENGTH_LONG).show();
        });


        btn_no.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void clickUnFavoriteFilm(MovieMainHome movieMainHome){
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_watch_film_from_time);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        TextView tv_title = dialog.findViewById(R.id.tv_at_time);
        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);

        tv_title.setText("Bạn có muốn xóa film " + movieMainHome.getName() + " khỏi mục film yêu thích của bạn?");


        btn_yes.setOnClickListener(v -> {
            collectionReferenceFilmFavorite.whereEqualTo("idFilm", movieMainHome.getId() +"")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            collectionReferenceFilmFavorite.document(documentId)
                                    .delete();
                        }
                    });
            mFilmFavoriteList.remove(movieMainHome);
            if(mFilmFavoriteList.size() == 0){
                binding.tvSizeFavorite.setVisibility(View.VISIBLE);
            }
            filmFavoriteAdapter.notifyDataSetChanged();
            dialog.dismiss();
            Toast.makeText(mMainActivity, "Bạn đã xóa film thành công",Toast.LENGTH_LONG).show();
        });

        btn_no.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void getIdUser(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mMainActivity);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(acct == null && accessToken == null){
            final Dialog dialog = new Dialog(mMainActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_ask_login);

            Window window = dialog.getWindow();
            if(window == null){
                return;
            }

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.gravity = Gravity.CENTER;
            window.setAttributes(windowAttributes);

            TextView tv_title = dialog.findViewById(R.id.tv_at_time);
            Button btn_login = dialog.findViewById(R.id.btn_login);
            Button btn_no = dialog.findViewById(R.id.btn_no);

            tv_title.setText("Bạn cần đăng nhập để biết thông tin này!");

            btn_login.setOnClickListener(v -> {
                Intent intent = new Intent(mMainActivity, LoginActivity.class);
                startActivity(intent);
            });

            btn_no.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
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
        collectionReferenceHistory.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                int check = 0;
                QuerySnapshot snapshot = task.getResult();
                for (QueryDocumentSnapshot doc : snapshot) {
                    String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                    callApiGetHistoryWatchFilm(Integer.parseInt(idFilm1));
                    check = 1;
                }
                if(check == 1){
                    binding.tvSizeHistory.setVisibility(View.INVISIBLE);
                }
                else{
                    binding.tvSizeHistory.setVisibility(View.VISIBLE);
                }
                binding.loadPersonPage.setVisibility(View.INVISIBLE);
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
        collectionReferenceFilmFavorite.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                int check = 0;
                QuerySnapshot snapshot = task.getResult();
                for (QueryDocumentSnapshot doc : snapshot) {
                    String idFilm1 = Objects.requireNonNull(doc.get("idFilm")).toString();
                    callApiGetFilmFavorite(Integer.parseInt(idFilm1));
                    check = 1;
                }
                if(check == 1){
                    binding.tvSizeFavorite.setVisibility(View.INVISIBLE);
                }else {
                    binding.tvSizeFavorite.setVisibility(View.VISIBLE);
                }
                binding.loadPersonPage.setVisibility(View.INVISIBLE);
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