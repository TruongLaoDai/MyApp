package com.smile.watchmovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.activity.DetailFilmActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ItemFilmFavoriteBinding;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.my_interface.IClickItemUnFavoriteListener;

import java.util.List;

public class FilmFavoriteAdapter extends RecyclerView.Adapter<FilmFavoriteAdapter.FilmFavoriteViewHolder> {

    private List<FilmMainHome> movieMainHomeList;
    private final Context context;
    private final IClickItemUnFavoriteListener iClickItemUnFavoriteListener;

    public FilmFavoriteAdapter(Context context, IClickItemUnFavoriteListener iClickItemUnFavoriteListener) {
        this.context = context;
        this.iClickItemUnFavoriteListener = iClickItemUnFavoriteListener;
    }

    public void setData(List<FilmMainHome> movieMainHomeList) {
        this.movieMainHomeList = movieMainHomeList;
    }

    @NonNull
    @Override
    public FilmFavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FilmFavoriteAdapter.FilmFavoriteViewHolder(ItemFilmFavoriteBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilmFavoriteViewHolder holder, int position) {
        FilmMainHome movieMainHome = movieMainHomeList.get(position);
        if (movieMainHome == null) {
            return;
        }

        Glide.with(context).load(movieMainHome.getAvatar())
                .error(R.drawable.ic_baseline_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.binding.ivImageFilm);
        int episodesTotal = movieMainHome.getEpisodesTotal();
        if (episodesTotal == 0) {
            holder.binding.tvEpisodesTotal.setText(context.getString(R.string.one_episode));
        } else {
            holder.binding.tvEpisodesTotal.setText(context.getString(R.string.episode_total, movieMainHome.getEpisodesTotal()));
        }

        holder.binding.tvStar.setText(context.getString(R.string.film_start, movieMainHome.getStar()));
        holder.binding.tvNameFilm.setText(movieMainHome.getName());
        holder.binding.layoutFilm.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailFilmActivity.class);
            intent.putExtra("id_detail_film", movieMainHome.getId());
            context.startActivity(intent);
        });
        holder.binding.ivFavorite.setOnClickListener(v -> iClickItemUnFavoriteListener.onClickUnFavoriteListener(movieMainHome));
    }

    @Override
    public int getItemCount() {
        if (movieMainHomeList != null) {
            return movieMainHomeList.size();
        }
        return 0;
    }

    public static class FilmFavoriteViewHolder extends RecyclerView.ViewHolder {

        private final ItemFilmFavoriteBinding binding;

        public FilmFavoriteViewHolder(ItemFilmFavoriteBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
