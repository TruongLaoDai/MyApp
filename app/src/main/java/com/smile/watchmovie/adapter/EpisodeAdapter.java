package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.WatchFilmActivity;
import com.smile.watchmovie.databinding.ItemEpisodeBinding;
import com.smile.watchmovie.fragment.IntroduceFilmFragment;
import com.smile.watchmovie.model.SubFilm;

import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    public List<SubFilm> subFilmList;
    public final WatchFilmActivity mWatchFilmActivity;
    public final IntroduceFilmFragment mIntroduceFilmFragment;

    public EpisodeAdapter(WatchFilmActivity watchFilmActivity, IntroduceFilmFragment mIntroduceFilmFragment) {
        this.mWatchFilmActivity = watchFilmActivity;
        this.mIntroduceFilmFragment = mIntroduceFilmFragment;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SubFilm> subVideoList) {
        this.subFilmList = subVideoList;
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
        SubFilm subFilm = subFilmList.get(position);
        if (subFilm == null) {
            return;
        }
        holder.itemEpisodeBinding.tvEpisode.setText(mWatchFilmActivity.getString(R.string.episode_film, subFilm.getEpisode()));
        holder.itemEpisodeBinding.layoutEpisode.setOnClickListener(v ->{
            mWatchFilmActivity.playVideo(subFilm);
            mIntroduceFilmFragment.episodeFilmPlaying(subFilm);
        });
        if(subFilm.getIsWatching()){
            holder.itemEpisodeBinding.tvEpisode.setBackgroundResource(R.drawable.botron_background_click);
            holder.itemEpisodeBinding.tvEpisode.setTextColor(Color.parseColor("#1877F2"));
        }
        else{
            holder.itemEpisodeBinding.tvEpisode.setBackgroundResource(R.drawable.botron_background);
            holder.itemEpisodeBinding.tvEpisode.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        if (subFilmList != null)
            return subFilmList.size();
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

