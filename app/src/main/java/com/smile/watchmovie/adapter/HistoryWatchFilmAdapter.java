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
import com.smile.watchmovie.databinding.ItemFilmHistoryBinding;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.listener.IClickItemDeleteHistoryListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryWatchFilmAdapter extends RecyclerView.Adapter<HistoryWatchFilmAdapter.HistoryWatchFilmViewHolder> {

    private List<FilmMainHome> filmMainHomeList;
    private final Context context;
    private final IClickItemDeleteHistoryListener itemDeleteHistoryListener;

    public HistoryWatchFilmAdapter(Context context, IClickItemDeleteHistoryListener itemDeleteHistoryListener) {
        this.context = context;
        this.itemDeleteHistoryListener = itemDeleteHistoryListener;
    }

    public void setData(List<FilmMainHome> movieMainHomeList) {
        this.filmMainHomeList = movieMainHomeList;
    }

    @NonNull
    @Override
    public HistoryWatchFilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryWatchFilmAdapter.HistoryWatchFilmViewHolder(ItemFilmHistoryBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryWatchFilmViewHolder holder, int position) {
        FilmMainHome filmMainHome = filmMainHomeList.get(position);
        if (filmMainHome == null) {
            return;
        }

        Glide.with(context).load(filmMainHome.getAvatar())
                .error(R.drawable.ic_baseline_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.binding.ivImageFilm);
        int episodesTotal = filmMainHome.getEpisodesTotal();
        if (episodesTotal == 0) {
            holder.binding.tvEpisodesTotal.setText(context.getString(R.string.one_episode));
        } else {
            holder.binding.tvEpisodesTotal.setText(context.getString(R.string.episode_total, filmMainHome.getEpisodesTotal()));
        }

        //holder.binding.tvStar.setText(context.getString(R.string.film_start, filmMainHome.getStar()));
        holder.binding.tvNameFilm.setText(filmMainHome.getName());
        holder.binding.layoutFilm.setOnClickListener(view -> {
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
        holder.binding.ivDeleteHistory.setOnClickListener(v -> itemDeleteHistoryListener.onClickDeleteHistoryListener(filmMainHome));
    }

    @Override
    public int getItemCount() {
        if(filmMainHomeList != null){
            return filmMainHomeList.size();
        }
        return 0;
    }

    public static class HistoryWatchFilmViewHolder extends RecyclerView.ViewHolder{

        private final ItemFilmHistoryBinding binding;

        public HistoryWatchFilmViewHolder(@NonNull ItemFilmHistoryBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
