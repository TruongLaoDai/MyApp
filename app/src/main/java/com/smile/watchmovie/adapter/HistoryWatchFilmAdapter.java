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
import com.smile.watchmovie.activity.PlayerActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ItemFilmHistoryBinding;
import com.smile.watchmovie.listener.IClickItemDeleteHistoryListener;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.HistoryWatchFilm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryWatchFilmAdapter extends RecyclerView.Adapter<HistoryWatchFilmAdapter.HistoryWatchFilmViewHolder> {

    private final Context context;
    private QuerySnapshot queryDocumentSnapshots;
    private IClickItemDeleteHistoryListener deleteHistoryListener;

    public HistoryWatchFilmAdapter(Context context) {
        this.context = context;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(QuerySnapshot queryDocumentSnapshots) {
        this.queryDocumentSnapshots = queryDocumentSnapshots;
        notifyDataSetChanged();
    }

    public void setDeleteHistoryListener(IClickItemDeleteHistoryListener deleteHistoryListener){
        this.deleteHistoryListener = deleteHistoryListener;
    }

    @NonNull
    @Override
    public HistoryWatchFilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryWatchFilmAdapter.HistoryWatchFilmViewHolder(ItemFilmHistoryBinding.inflate(inflater, parent, false));
    }

    private String messagePlayAtTime(long time) {
        int hour = (int) (time / 1000 / 3600);
        int minute = (int) ((time / 1000 - hour * 3600) / 60);
        int second = (int) (time / 1000 - hour * 3600 - minute * 60);
        if(hour != 0)
            return hour + ":" + minute + ":" + second;
        return minute + ":" + second;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryWatchFilmViewHolder holder, int position) {
        HistoryWatchFilm historyWatchFilm = queryDocumentSnapshots.getDocuments().get(position).toObject(HistoryWatchFilm.class);

        if (historyWatchFilm == null) {
            return;
        }
        historyWatchFilm.setDocumentID(queryDocumentSnapshots.getDocuments().get(position).getId());

        Glide.with(context).load(historyWatchFilm.getAvatarFilm())
                .error(R.drawable.ic_baseline_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_gray)
                .into(holder.binding.ivImageFilm);

        holder.binding.tvDayWatched.setVisibility(View.VISIBLE);
//        holder.binding.tvDurationWatched.setVisibility(View.VISIBLE);
        holder.binding.ivMoreAction.setVisibility(View.VISIBLE);
//        holder.binding.tvEpisodesTotal.setVisibility(View.GONE);
//        holder.binding.tvViewNumber.setVisibility(View.GONE);

//        holder.binding.tvDurationWatched.setText(context.getString(R.string.duration_watch, messagePlayAtTime(historyWatchFilm.getDuration())));
        holder.binding.tvDayWatched.setText(context.getString(R.string.day_watch, historyWatchFilm.getDayWatch()));

        if (historyWatchFilm.getId_film() % 2 == 0) {
            holder.binding.loutPremium.setVisibility(ViewGroup.VISIBLE);
        } else {
            holder.binding.loutPremium.setVisibility(ViewGroup.GONE);
        }

        holder.binding.tvNameFilm.setText(historyWatchFilm.getNameFilm());
        holder.binding.layoutFilm.setOnClickListener(view ->
                ApiService.apiService
                        .getFilmDetail("7da353b8a3246f851e0ee436d898a26d", historyWatchFilm.getId_film())
                        .enqueue(new Callback<FilmDetailResponse>() {
                            @SuppressLint("StringFormatMatches")
                            @Override
                            public void onResponse(@NonNull Call<FilmDetailResponse> call, @NonNull Response<FilmDetailResponse> response) {
                                FilmDetailResponse cinema = response.body();
                                if (cinema != null) {
                                    FilmMainHome filmPlay;
                                    filmPlay = cinema.getData();
                                    Intent intent = new Intent(context, PlayerActivity.class);
                                    intent.putExtra("film", filmPlay);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<FilmDetailResponse> call, @NonNull Throwable t) {
                                Toast.makeText(context, "Error Get Film", Toast.LENGTH_SHORT).show();

                            }
                        }));
        holder.binding.ivMoreAction.setOnClickListener(v -> deleteHistoryListener.onClickDeleteHistoryListener(historyWatchFilm));
    }

    @Override
    public int getItemCount() {
        if (queryDocumentSnapshots != null) {
            return queryDocumentSnapshots.size();
        }
        return 0;
    }

    public static class HistoryWatchFilmViewHolder extends RecyclerView.ViewHolder {

        private final ItemFilmHistoryBinding binding;

        public HistoryWatchFilmViewHolder(@NonNull ItemFilmHistoryBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
