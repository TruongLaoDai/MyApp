package com.smile.watchmovie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smile.watchmovie.R
import com.smile.watchmovie.activity.WatchFilmActivity
import com.smile.watchmovie.api.ApiService
import com.smile.watchmovie.databinding.ItemFilmDetailBinding
import com.smile.watchmovie.model.FilmDetailResponse
import com.smile.watchmovie.model.FilmMainHome
import com.smile.watchmovie.utils.OtherUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmRelativeAdapter(private val context: Context) :
    RecyclerView.Adapter<FilmRelativeAdapter.ViewHolder>() {
    private var list = ArrayList<FilmMainHome>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: ArrayList<FilmMainHome>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFilmDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.apply {
                    Glide.with(context).load(this@with.avatar)
                        .error(R.drawable.ic_baseline_broken_image_gray)
                        .placeholder(R.drawable.ic_baseline_image_gray)
                        .into(ivImageFilm)

                    /* Kiểm tra phim vip hay không */
                    loutPremium.rootPremium.visibility = if (this@with.id % 2 == 0) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    tvNameFilm.text = this@with.name

                    tvCategory.text = context.getString(
                        R.string.category_film, when (this@with.categoryId) {
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

                    tvCreated.text =
                        context.getString(
                            R.string.day_watch,
                            OtherUtils().formatTime(this@with.created)
                        )

                    tvViewNumber.text =
                        context.getString(R.string.tv_view_number, this@with.viewNumber)

                    /* Hiển thị số tập phim */
                    tvEpisodesTotal.text = if (this@with.episodesTotal == 0) {
                        context.getString(R.string.one_episode)
                    } else {
                        context.getString(R.string.episode_total, this@with.episodesTotal)
                    }
                }
            }
        }
    }

    private fun playFilm(filmMainHome: FilmMainHome) {
        ApiService.apiService.getFilmDetail(context.getString(R.string.wsToken), filmMainHome.id)
            .enqueue(object : Callback<FilmDetailResponse?> {
                @SuppressLint("StringFormatMatches")
                override fun onResponse(
                    call: Call<FilmDetailResponse?>,
                    response: Response<FilmDetailResponse?>
                ) {
                    val cinema = response.body()
                    if (cinema != null) {
                        val intent = Intent(context, WatchFilmActivity::class.java)
                        intent.putExtra("film", cinema.data)
                        context.startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<FilmDetailResponse?>, t: Throwable) {
                    Toast.makeText(context, "Tải dữ liệu phim không thành công", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val binding: ItemFilmDetailBinding) :
        RecyclerView.ViewHolder(binding.root)
}