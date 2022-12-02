package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.R;
import com.smile.watchmovie.WatchFilmActivity;
import com.smile.watchmovie.databinding.ItemEpisodeBinding;
import com.smile.watchmovie.model.SubVideo;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    public List<SubVideo> subVideoList;
    public final WatchFilmActivity mWatchFilmActivity;

    public EpisodeAdapter(WatchFilmActivity watchFilmActivity) {
        this.mWatchFilmActivity = watchFilmActivity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SubVideo> subVideoList) {
        this.subVideoList = subVideoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new EpisodeViewHolder(ItemEpisodeBinding.inflate(inflater, parent, false));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SubVideo subVideo = subVideoList.get(position);
        if (subVideo == null) {
            return;
        }
        holder.itemEpisodeBinding.tvEpisode.setText(mWatchFilmActivity.getString(R.string.episode_film, subVideo.getEpisode()));
        holder.itemEpisodeBinding.layoutEpisode.setOnClickListener(v -> mWatchFilmActivity.playVideo(subVideo));
        if(subVideo.getIsWatching()){
            holder.itemEpisodeBinding.tvEpisode.setTextColor(Color.parseColor("#F44336"));
        }
        else{
            holder.itemEpisodeBinding.tvEpisode.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        if (subVideoList != null)
            return subVideoList.size();
        return 0;
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        private final ItemEpisodeBinding itemEpisodeBinding;
        public EpisodeViewHolder(ItemEpisodeBinding itemEpisodeBinding) {
            super(itemEpisodeBinding.getRoot());
            this.itemEpisodeBinding = itemEpisodeBinding;
        }
    }
}

