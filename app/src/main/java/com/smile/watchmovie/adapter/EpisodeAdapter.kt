package com.smile.watchmovie.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smile.watchmovie.R
import com.smile.watchmovie.databinding.ItemEpisodeBinding
import com.smile.watchmovie.model.SubFilm

class EpisodeAdapter(private val listener: OnListener) :
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
                    tvEpisode.apply {
                        text = context.getString(R.string.episode_film, this@with.episode)
                        setBackgroundResource(
                            if (this@with.isWatching) {
                                R.drawable.bg_sub_film_clicked
                            } else {
                                R.drawable.bg_corner_dialog
                            }
                        )
                    }

                    root.setOnClickListener {
                        listener.onClickSubFilm(this@with)
                    }
                }
            }
        }
    }

    override fun getItemCount() = subFilmList.size

    inner class ViewHolder(val binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnListener {
        fun onClickSubFilm(subFilm: SubFilm)
    }
}