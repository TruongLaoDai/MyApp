package com.smile.watchmovie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smile.watchmovie.R
import com.smile.watchmovie.databinding.ItemFilmBinding
import com.smile.watchmovie.model.FilmMainHome

class HomeFragmentAdapter(
    private val context: Context,
    private val listener: OnClickListener
) : RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder>() {

    private var list = ArrayList<FilmMainHome>()

    @SuppressLint("NotifyDataSetChanged")
    internal fun setData(newList: List<FilmMainHome>) {
        list = newList as ArrayList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.apply {
                    Glide.with(context).load(this@with.avatar)
                        .error(R.drawable.ic_baseline_broken_image_gray)
                        .placeholder(R.drawable.ic_baseline_image_gray).into(ivImageFilm)

                    loutPremium.root.visibility = if (this@with.id % 2 == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    tvNameFilm.text = this@with.name

                    root.setOnClickListener {
                        listener.openFilm(this@with.id)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val binding: ItemFilmBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnClickListener {
        fun openFilm(filmId: Int)
    }
}