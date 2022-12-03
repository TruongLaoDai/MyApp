package com.smile.watchmovie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smile.watchmovie.adapter.BannerAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityDetailFilmBinding;
import com.smile.watchmovie.model.FilmFavorite;
import com.smile.watchmovie.model.FilmFavorites;
import com.smile.watchmovie.model.MovieDetailResponse;
import com.smile.watchmovie.model.MovieMainHome;
import com.smile.watchmovie.model.Banner;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFilmActivity extends AppCompatActivity {

    private BannerAdapter mBannerAdapter;
    private List<Banner> mBannerList;
    private Timer mTimer;
    private ActivityDetailFilmBinding binding;
    private String idUser;
    private int currentImage;
    private int idFilm;
    private List<FilmFavorite> filmFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_film);

        binding = ActivityDetailFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutPageDetail.setVisibility(View.INVISIBLE);
        getIdUser();
        filmFavorites = new ArrayList<>();

        idFilm = getIntent().getIntExtra("id_detail_film", 0);
        mBannerAdapter = new BannerAdapter(this);
        isFilmFavorite();
        currentImage = R.drawable.ic_baseline_favorite_border_24;
        Toast.makeText(DetailFilmActivity.this, filmFavorites.size()+"",Toast.LENGTH_SHORT).show();
        binding.ivNoFavorite.setOnClickListener(v -> {
            if(currentImage == R.drawable.ic_baseline_favorite_border_24){
                currentImage = R.drawable.ic_baseline_favorite_24;
                Toast.makeText(DetailFilmActivity.this, "Bạn đã thích film", Toast.LENGTH_LONG).show();
            }
            else{
                currentImage = R.drawable.ic_baseline_favorite_border_24;
                Toast.makeText(DetailFilmActivity.this, "Bạn đã bỏ thích film", Toast.LENGTH_LONG).show();
            }
            binding.ivNoFavorite.setImageResource(currentImage);
        });
        callApiGetDetailFilm(idFilm);
    }

    private void getIdUser(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(acct == null && accessToken == null){
            binding.ivNoFavorite.setVisibility(View.INVISIBLE);
        }
        else if(acct != null){
            idUser = acct.getId();
        }
        else if(!accessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    (object, response) -> {
                        // Application code
                        try {
                            assert object != null;
                            idUser = (String) object.get("id");
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

    private void callApiGetDetailFilm(int id_film) {
        ApiService.apiService.getFilmDetail("7da353b8a3246f851e0ee436d898a26d", id_film).enqueue(new Callback<MovieDetailResponse>() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onResponse(@NonNull Call<MovieDetailResponse> call, @NonNull Response<MovieDetailResponse> response) {
                MovieDetailResponse cinema = response.body();
                if (cinema != null) {
                    binding.loadPageDetail.setVisibility(View.INVISIBLE);
                    binding.layoutPageDetail.setVisibility(View.VISIBLE);
                    MovieMainHome movieMainHome;
                    movieMainHome = cinema.getData();
                    mBannerList = setPhotoList(movieMainHome.getPoster());
                    mBannerAdapter.setData(mBannerList);
                    binding.vpPoster.setAdapter(mBannerAdapter);
                    autoSlideImage();
                    Glide.with(DetailFilmActivity.this).load(movieMainHome.getAvatar()).into(binding.imgLogoFilm);
                    binding.tvNameFilm.setText(movieMainHome.getName());
                    int episodesTotal = movieMainHome.getEpisodesTotal();
                    String viewEpisodesTotal;
                    if (episodesTotal == 0) {
                        viewEpisodesTotal = "Status: Full";
                        binding.tvEpisodesTotal.setText(viewEpisodesTotal);
                    } else {
                        viewEpisodesTotal = "Status: " + movieMainHome.getSubVideoList().size() + "/" + episodesTotal;
                        binding.tvEpisodesTotal.setText(viewEpisodesTotal);
                    }
                    String[] detail = movieMainHome.getDescriptionEx().split("<br>");
                    binding.tvDirectors.setText(detail[0].trim());
                    binding.tvHakerekNain.setText(detail[1].trim());
                    binding.tvEstrelas.setText(detail[2].trim());
                    if (movieMainHome.getSubVideoList() != null) {
                        binding.tvDurasaun.setText(getString(R.string.tv_duration, detail[3]));
                    } else {
                        binding.tvDurasaun.setText(detail[3].trim());
                    }
                    binding.tvViewNumber.setText(getString(R.string.tv_view_number, movieMainHome.getViewNumber()));
                    String[] date = movieMainHome.getCreated().split("-", 3);
                    binding.tvCreated.setText(getString(R.string.tv_created,date[2].substring(0, 2), date[1], date[0]));
                    binding.tvStar.setText(getString(R.string.tv_star_rating,movieMainHome.getStar()));
                    binding.tvDescription.setText(movieMainHome.getDescription());
                    binding.ciPoster.setViewPager(binding.vpPoster);
                    mBannerAdapter.registerDataSetObserver(binding.ciPoster.getDataSetObserver());

                    binding.layoutWatchFilm.setOnClickListener(v -> {
                        Intent intent = new Intent(DetailFilmActivity.this, WatchFilmActivity.class);
                        intent.putExtra("movie", movieMainHome);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(DetailFilmActivity.this, "Error Get Video", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private List<Banner> setPhotoList(String photos) {
        List<Banner> photoList = new ArrayList<>();
        String[] photo = photos.split(",");
        for (String pt : photo) {
            Banner photo1 = new Banner(pt);
            photoList.add(photo1);
        }
        return photoList;
    }

    private void autoSlideImage() {
        if (mBannerList == null || mBannerList.isEmpty()) {
            return;
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    int currentItem = binding.vpPoster.getCurrentItem();
                    int totalItem = mBannerList.size() - 1;
                    if (currentItem < totalItem) {
                        currentItem++;
                        binding.vpPoster.setCurrentItem(currentItem);
                    } else {
                        binding.vpPoster.setCurrentItem(0);
                    }
                });
            }

        },500, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("favorite_film_"+idUser);
        if(currentImage == R.drawable.ic_baseline_favorite_24){
            FilmFavorite filmFavorite = new FilmFavorite(idFilm);
            FilmFavorites filmFavoritesModel = new FilmFavorites();
            ArrayList<FilmFavorite> favorites = new ArrayList<>();
            favorites.add(filmFavorite);
            filmFavoritesModel.setFilmFavorites(favorites);
            myRef.setValue(filmFavorite);
        }
    }

    public void isFilmFavorite(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("favorite_film_"+idUser);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               FilmFavorite favorites = snapshot.getValue(FilmFavorite.class);
                if(filmFavorites != null){
                    //ArrayList<FilmFavorite> favorites = filmFavorites.getFilmFavorites();
                    assert favorites != null;
                    if(favorites.getIdFilm() == idFilm){
                        currentImage = R.drawable.ic_baseline_favorite_24;
                    }
                    else{
                        currentImage = R.drawable.ic_baseline_favorite_border_24;
                    }
                    binding.ivNoFavorite.setImageResource(currentImage);
                    //Toast.makeText(DetailFilmActivity.this, filmFavorite.getIdFilm()+"",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}