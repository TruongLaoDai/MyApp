package com.smile.watchmovie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smile.watchmovie.R
import com.smile.watchmovie.activity.WatchFilmActivity
import com.smile.watchmovie.databinding.ItemEpisodeBinding
import com.smile.watchmovie.fragment.IntroduceFilmFragment
import com.smile.watchmovie.model.SubFilm

class EpisodeAdapter(private val context: Context) :
    RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {
    private var subFilmList = ArrayList<SubFilm>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newSubVideoList: List<SubFilm>) {
        subFilmList = newSubVideoList as ArrayList<SubFilm>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(subFilmList[position]) {
                binding.apply {
                    tvEpisode.text = context.getString(R.string.episode_film, this@with.episode)
                }
            }
        }
    }

//    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
//    override fun onBindViewHolder(
//        holder: EpisodeViewHolder,
//        @SuppressLint("RecyclerView") position: Int
//    ) {
//        val subFilm = subFilmList!![position] ?: return
//        holder.itemEpisodeBinding.tvEpisode.text =
//            mWatchFilmActivity.getString(R.string.episode_film, subFilm.episode)
//        holder.itemEpisodeBinding.layoutEpisode.setOnClickListener { v: View? ->
//            mWatchFilmActivity.playFilm(subFilm)
//            mIntroduceFilmFragment.episodeFilmPlaying(subFilm)
//        }
//        if (subFilm.isWatching) {
//            holder.itemEpisodeBinding.tvEpisode.setBackgroundResource(R.drawable.botron_background_click)
//            holder.itemEpisodeBinding.tvEpisode.setTextColor(Color.parseColor("#1877F2"))
//        } else {
//            holder.itemEpisodeBinding.tvEpisode.setBackgroundResource(R.drawable.bg_corner_dialog)
//            holder.itemEpisodeBinding.tvEpisode.setTextColor(Color.parseColor("#000000"))
//        }
//    }

    override fun getItemCount() = subFilmList.size

    inner class ViewHolder(val binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root)
}