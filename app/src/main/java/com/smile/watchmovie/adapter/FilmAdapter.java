package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ItemFilmBinding;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<FilmMainHome> filmMainHomeList;

    public FilmAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<FilmMainHome> filmMainHomeList) {
        this.filmMainHomeList = filmMainHomeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FilmViewHolder(ItemFilmBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FilmViewHolder) {
            FilmMainHome filmMainHome = filmMainHomeList.get(position);
            if (filmMainHome == null) {
                return;
            }

            Glide.with(context).load(filmMainHome.getAvatar())
                    .error(R.drawable.ic_baseline_broken_image_gray)
                    .placeholder(R.drawable.ic_baseline_image_gray)
                    .into(((FilmViewHolder) holder).itemFilmBinding.ivImageFilm);

            if (filmMainHome.getId() % 2 == 0) {
                ((FilmViewHolder) holder).itemFilmBinding.loutPremium.setVisibility(ViewGroup.VISIBLE);
            } else {
                ((FilmViewHolder) holder).itemFilmBinding.loutPremium.setVisibility(ViewGroup.GONE);
            }

            ((FilmViewHolder) holder).itemFilmBinding.tvNameFilm.setText(filmMainHome.getName());
            ((FilmViewHolder) holder).itemFilmBinding.layoutFilm.setOnClickListener(view -> playFilm(filmMainHome));
        }
    }

    private void playFilm(FilmMainHome filmMainHome) {
        ApiService.apiService.getFilmDetail(context.getString(R.string.wsToken), filmMainHome.getId()).enqueue(new Callback<FilmDetailResponse>() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onResponse(@NonNull Call<FilmDetailResponse> call, @NonNull Response<FilmDetailResponse> response) {
                FilmDetailResponse cinema = response.body();
                if (cinema != null) {
                    FilmMainHome filmPlay;
                    filmPlay = cinema.getData();
                    Intent intent = new Intent(context, WatchFilmActivity.class);
                    intent.putExtra("film", filmPlay);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FilmDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error Get Film", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        if (filmMainHomeList != null) {
            return filmMainHomeList.size();
        }
        return 0;
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {

        private final ItemFilmBinding itemFilmBinding;

        public FilmViewHolder(@NonNull ItemFilmBinding itemFilmBinding) {
            super(itemFilmBinding.getRoot());
            this.itemFilmBinding = itemFilmBinding;
        }
    }
}
