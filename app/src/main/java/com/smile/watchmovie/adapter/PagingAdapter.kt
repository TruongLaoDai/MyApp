package com.smile.watchmovie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smile.watchmovie.R
import com.smile.watchmovie.databinding.ItemFilmDetailBinding
import com.smile.watchmovie.model.FilmMainHome
import com.smile.watchmovie.utils.OtherUtils

class PagingAdapter(
    private val context: Context,
    private val listener: OnClickListener
) :
    PagingDataAdapter<FilmMainHome, PagingAdapter.ViewHolder>(Comparator) {
    inner class ViewHolder(val binding: ItemFilmDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder) {
            with(binding) {
                item?.let {
                    Glide.with(context).load(item.avatar)
                        .error(R.drawable.ic_baseline_broken_image_gray)
                        .placeholder(R.drawable.ic_baseline_image_gray)
                        .into(ivImageFilm)

                    /* Hiển thị số tập phim */
                    val episodesTotal = item.episodesTotal
                    tvEpisodesTotal.text = if (episodesTotal == 0) {
                        context.getString(R.string.one_episode)
                    } else {
                        context.getString(R.string.episode_total, episodesTotal)
                    }

                    /* Số lượt xem */
                    tvViewNumber.text = context.getString(R.string.tv_view_number, item.viewNumber)

                    /* Kiểm tra phim vip hay không */
                    loutPremium.rootPremium.visibility = if (item.id % 2 == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    /* Ngày ra mắt */
                    tvCreated.text =
                        context.getString(R.string.day_watch, OtherUtils().formatTime(item.created))

                    /* Tên phim */
                    tvNameFilm.text = item.name

                    layoutFilm.setOnClickListener {
                        listener.playFilm(item)
                    }

                    tvCategory.text = context.getString(
                        R.string.category_film, when (item.categoryId) {
                            14 -> "Hoạt hình"
                            6 -> "Hành động"
                            4 -> "Kinh dị"
                            11 -> "Trinh thám"
                            12 -> "Hài kịch"
                            13 -> "Lãng mạn"
                            15 -> "Phiêu lưu"
                            else -> "Chưa xác định"
                        }
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFilmDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    object Comparator : DiffUtil.ItemCallback<FilmMainHome>() {
        override fun areItemsTheSame(oldItem: FilmMainHome, newItem: FilmMainHome): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: FilmMainHome, newItem: FilmMainHome): Boolean {
            return oldItem == newItem
        }
    }

    interface OnClickListener {
        fun playFilm(film: FilmMainHome)
    }
}