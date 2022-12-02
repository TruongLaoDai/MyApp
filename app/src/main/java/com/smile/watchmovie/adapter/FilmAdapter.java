package com.smile.watchmovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.DetailFilmActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ItemFilmBinding;
import com.smile.watchmovie.model.MovieMainHome;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<MovieMainHome> movieMainHomeList;

    public FilmAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<MovieMainHome> movieMainHomeList){
        this.movieMainHomeList = movieMainHomeList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FilmViewHolder(ItemFilmBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FilmViewHolder){
            MovieMainHome movieMainHome = movieMainHomeList.get(position);
            if (movieMainHome == null) {
                return;
            }

            Glide.with(context).load(movieMainHome.getAvatar())
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(((FilmViewHolder) holder).itemFilmBinding.ivImageFilm);
            int episodesTotal = movieMainHome.getEpisodesTotal();
            if (episodesTotal == 0) {
                ((FilmViewHolder) holder).itemFilmBinding.tvEpisodesTotal.setText(context.getString(R.string.one_episode));
            } else {
                ((FilmViewHolder) holder).itemFilmBinding.tvEpisodesTotal.setText(context.getString(R.string.episode_total, movieMainHome.getEpisodesTotal()));
            }

            ((FilmViewHolder) holder).itemFilmBinding.tvStar.setText(context.getString(R.string.film_start, movieMainHome.getStar()));
            ((FilmViewHolder) holder).itemFilmBinding.tvNameFilm.setText(movieMainHome.getName());
            ((FilmViewHolder) holder).itemFilmBinding.layoutFilm.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailFilmActivity.class);
                intent.putExtra("id_detail_film", movieMainHome.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if(movieMainHomeList != null){
            return movieMainHomeList.size();
        }
        return 0;
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder{

        private final ItemFilmBinding itemFilmBinding;

        public FilmViewHolder(@NonNull ItemFilmBinding itemFilmBinding){
            super(itemFilmBinding.getRoot());
            this.itemFilmBinding = itemFilmBinding;
        }
    }
}
