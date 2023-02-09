package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ItemFilmDetailBinding;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmSearchAdapter extends RecyclerView.Adapter<FilmSearchAdapter.FilmSearchViewHolder> {

    private final Context context;
    private List<FilmMainHome> filmMainHomeList;

    public FilmSearchAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<FilmMainHome> movieMainHomeList) {
        this.filmMainHomeList = movieMainHomeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilmSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FilmSearchViewHolder(ItemFilmDetailBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilmSearchViewHolder holder, int position) {
        FilmMainHome filmMainHome = filmMainHomeList.get(position);
        if (filmMainHome == null) {
            return;
        }

        Glide.with(context).load(filmMainHome.getAvatar())
                .error(R.drawable.ic_baseline_broken_image_gray)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.itemFilmDetailBinding.ivImageFilm);
        int episodesTotal = filmMainHome.getEpisodesTotal();
        if (episodesTotal == 0) {
            holder.itemFilmDetailBinding.tvEpisodesTotal.setText(context.getString(R.string.one_episode));
        } else {
            holder.itemFilmDetailBinding.tvEpisodesTotal.setText(context.getString(R.string.episode_total, filmMainHome.getEpisodesTotal()));
        }
        holder.itemFilmDetailBinding.tvViewNumber.setText(context.getString(R.string.tv_view_number, filmMainHome.getViewNumber()));

        holder.itemFilmDetailBinding.tvNameFilm.setText(filmMainHome.getName());
        holder.itemFilmDetailBinding.layoutFilm.setOnClickListener(view -> {
            ApiService.apiService.getFilmDetail("7da353b8a3246f851e0ee436d898a26d", filmMainHome.getId()).enqueue(new Callback<FilmDetailResponse>() {
                @SuppressLint("StringFormatMatches")
                @Override
                public void onResponse(@NonNull Call<FilmDetailResponse> call, @NonNull Response<FilmDetailResponse> response) {
                    FilmDetailResponse cinema = response.body();
                    if (cinema != null) {
                        FilmMainHome filmPlay;
                        filmPlay = cinema.getData();
                        Intent intent = new Intent(context, WatchFilmActivity.class);
                        intent.putExtra("movie", filmPlay);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FilmDetailResponse> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Error Get Film", Toast.LENGTH_SHORT).show();

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        if (filmMainHomeList != null) {
            return filmMainHomeList.size();
        }
        return 0;
    }

    public static class FilmSearchViewHolder extends RecyclerView.ViewHolder {

        private final ItemFilmDetailBinding itemFilmDetailBinding;

        public FilmSearchViewHolder(@NonNull ItemFilmDetailBinding itemFilmDetailBinding) {
            super(itemFilmDetailBinding.getRoot());
            this.itemFilmDetailBinding = itemFilmDetailBinding;
        }
    }
}
