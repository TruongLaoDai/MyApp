package com.smile.watchmovie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smile.watchmovie.R
import com.smile.watchmovie.databinding.ItemFavoriteFilmBinding
import com.smile.watchmovie.model.HistoryWatchFilm

class WatchHistoryAdapter(
    private val context: Context,
    private val listener: OnListener
) :
    RecyclerView.Adapter<WatchHistoryAdapter.ViewHolder>() {
    private var list = ArrayList<HistoryWatchFilm>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFavoriteFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.apply {
                    Glide.with(context).load(this@with.avatarFilm)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .placeholder(R.drawable.ic_baseline_image_gray)
                        .into(ivImageFilm)

                    tvNameFilm.text = this@with.nameFilm

                    loutPremium.root.visibility = if (this@with.id_film % 2 == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    tvDateWatch.apply {
                        visibility = View.VISIBLE
                        text = context.getString(R.string.watch_date, this@with.dayWatch)
                    }

                    root.setOnClickListener {
                        listener.openFilm(this@with.id_film)
                    }

                    root.setOnLongClickListener {
                        listener.deleteWatchHistory(this@with.documentID)
                        true
                    }
                }
            }
        }
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<HistoryWatchFilm>) {
        list = newList as ArrayList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemFavoriteFilmBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnListener {
        fun openFilm(idFilm: Int)

        fun deleteWatchHistory(documentId: String)
    }
}