package com.smile.watchmovie.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smile.watchmovie.R
import com.smile.watchmovie.databinding.FrangmentDetailFilmBinding
import com.smile.watchmovie.model.FilmMainHome
import com.smile.watchmovie.utils.OtherUtils

class InfoFilmDialog(
    private val film: FilmMainHome,
    private val context: Context
) : BottomSheetDialogFragment() {
    private lateinit var binding: FrangmentDetailFilmBinding

    companion object {
        const val TAG = "InfoFilmDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrangmentDetailFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeData()
    }

    private fun initializeData() {
        binding.apply {
            Glide.with(context).load(film.avatar)
                .error(R.drawable.ic_baseline_broken_image_gray)
                .error(R.drawable.ic_baseline_broken_image_gray)
                .into(imgLogoFilm)
            tvNameFilm.text = film.name
            tvCreated.text =
                context.getString(R.string.day_watch, OtherUtils().formatTime(film.created))
            rate.text = context.getString(R.string.rate_film, film.star)
            tvDescription.text = film.description
            tvEpisodesTotal.text = if (film.episodesTotal == 0) {
                context.getString(R.string.one_episode)
            } else {
                context.getString(R.string.episode_total, film.episodesTotal)
            }
            tvCategory.text = context.getString(
                R.string.category_film, when (film.categoryId) {
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