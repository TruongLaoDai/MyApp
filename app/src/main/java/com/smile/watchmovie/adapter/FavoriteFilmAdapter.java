package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ItemFilmHistoryBinding;
import com.smile.watchmovie.listener.IClickItemUnFavoriteListener;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.FilmReaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFilmAdapter extends RecyclerView.Adapter<FavoriteFilmAdapter.FavoriteFilmViewHolder> {

    private final Context context;
    private QuerySnapshot queryDocumentSnapshots;
    private IClickItemUnFavoriteListener unFavoriteListener;

    public FavoriteFilmAdapter(Context context) {
        this.context = context;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(QuerySnapshot queryDocumentSnapshots) {
        this.queryDocumentSnapshots = queryDocumentSnapshots;
        notifyDataSetChanged();
    }

    public void setUnFavoriteListener(IClickItemUnFavoriteListener unFavoriteListener) {
        this.unFavoriteListener = unFavoriteListener;
    }

    @NonNull
    @Override
    public FavoriteFilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FavoriteFilmAdapter.FavoriteFilmViewHolder(ItemFilmHistoryBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteFilmViewHolder holder, int position) {
        FilmReaction filmFavorite = queryDocumentSnapshots.getDocuments().get(position).toObject(FilmReaction.class);

        if (filmFavorite == null) {
            return;
        }

        filmFavorite.setDocumentId(queryDocumentSnapshots.getDocuments().get(position).getId());

        Glide.with(context).load(filmFavorite.getAvatarFilm())
                .error(R.drawable.ic_baseline_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.binding.ivImageFilm);

        holder.binding.tvDayWatched.setVisibility(View.VISIBLE);
        holder.binding.tvDurationWatched.setVisibility(View.VISIBLE);
        holder.binding.ivMoreAction.setVisibility(View.VISIBLE);
        holder.binding.tvEpisodesTotal.setVisibility(View.GONE);
        holder.binding.tvViewNumber.setVisibility(View.GONE);

        holder.binding.tvDayWatched.setText(context.getString(R.string.day_favorite, filmFavorite.getDateReact()));

        if (filmFavorite.getIdFilm() % 2 == 0) {
            holder.binding.loutPremium.setVisibility(ViewGroup.VISIBLE);
        } else {
            holder.binding.loutPremium.setVisibility(ViewGroup.GONE);
        }

        holder.binding.tvNameFilm.setText(filmFavorite.getNameFilm());
        holder.binding.layoutFilm.setOnClickListener(view ->
                ApiService.apiService
                        .getFilmDetail("7da353b8a3246f851e0ee436d898a26d", filmFavorite.getIdFilm())
                        .enqueue(new Callback<FilmDetailResponse>() {
                            @SuppressLint("StringFormatMatches")
                            @Override
                            public void onResponse(@NonNull Call<FilmDetailResponse> call, @NonNull Response<FilmDetailResponse> response) {
                                FilmDetailResponse cinema = response.body();
                                if (cinema != null) {
                                    FilmMainHome filmPlay;
                                    filmPlay = cinema.getData();
                                    Intent intent = new Intent(context, WatchFilmActivity.class);
                                    intent.putExtra("film", filmPlay);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<FilmDetailResponse> call, @NonNull Throwable t) {
                                Toast.makeText(context, "Error Get Film", Toast.LENGTH_SHORT).show();

                            }
                        }));
        holder.binding.ivMoreAction.setOnClickListener(v -> unFavoriteListener.onClickUnFavoriteListener(filmFavorite));
    }

    @Override
    public int getItemCount() {
        if (queryDocumentSnapshots != null) {
            return queryDocumentSnapshots.size();
        }
        return 0;
    }

    public static class FavoriteFilmViewHolder extends RecyclerView.ViewHolder {

        private final ItemFilmHistoryBinding binding;

        public FavoriteFilmViewHolder(@NonNull ItemFilmHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
