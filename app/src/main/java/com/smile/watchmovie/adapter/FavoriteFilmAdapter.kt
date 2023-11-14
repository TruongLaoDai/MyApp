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
import com.smile.watchmovie.model.FilmReaction

class FavoriteFilmAdapter(
    private val context: Context,
    private val listener: OnListener
) :
    RecyclerView.Adapter<FavoriteFilmAdapter.ViewHolder>() {
    private var list = ArrayList<FilmReaction>()

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

                    loutPremium.root.visibility = if (this@with.idFilm % 2 == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    root.setOnClickListener {
                        listener.openFilm(this@with.idFilm)
                    }

                    root.setOnLongClickListener {
                        listener.deleteToFavorite(this@with.documentId)
                        true
                    }
                }
            }
        }
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<FilmReaction>) {
        list = newList as ArrayList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemFavoriteFilmBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnListener {
        fun openFilm(idFilm: Int)

        fun deleteToFavorite(documentId: String)
    }
}