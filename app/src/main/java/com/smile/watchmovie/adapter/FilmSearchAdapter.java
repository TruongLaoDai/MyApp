package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smile.watchmovie.activity.DetailFilmActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.databinding.ItemFilmDetailBinding;
import com.smile.watchmovie.model.FilmMainHome;

import java.util.List;

public class FilmSearchAdapter extends RecyclerView.Adapter<FilmSearchAdapter.FilmSearchViewHolder> {

    private final Context context;
    private List<FilmMainHome> movieMainHomeList;

    public FilmSearchAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<FilmMainHome> movieMainHomeList) {
        this.movieMainHomeList = movieMainHomeList;
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
        FilmMainHome movieMainHome = movieMainHomeList.get(position);
        if (movieMainHome == null) {
            return;
        }

        Glide.with(context).load(movieMainHome.getAvatar())
                .error(R.drawable.ic_baseline_broken_image_gray)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.itemFilmDetailBinding.ivImageFilm);
        int episodesTotal = movieMainHome.getEpisodesTotal();
        if (episodesTotal == 0) {
            holder.itemFilmDetailBinding.tvEpisodesTotal.setText(context.getString(R.string.one_episode));
        } else {
            holder.itemFilmDetailBinding.tvEpisodesTotal.setText(context.getString(R.string.episode_total, movieMainHome.getEpisodesTotal()));
        }
        holder.itemFilmDetailBinding.tvViewNumber.setText(context.getString(R.string.tv_view_number, movieMainHome.getViewNumber()));

        holder.itemFilmDetailBinding.tvNameFilm.setText(movieMainHome.getName());
        holder.itemFilmDetailBinding.layoutFilm.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailFilmActivity.class);
            intent.putExtra("id_detail_film", movieMainHome.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (movieMainHomeList != null) {
            return movieMainHomeList.size();
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
