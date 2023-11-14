package com.smile.watchmovie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.smile.watchmovie.repository.ApiRepository
import kotlinx.coroutines.Dispatchers

class FavoriteFilmActivityViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    fun getFilmDetail(header: String, filmId: Int) = liveData(Dispatchers.IO) {
        try {
            val getFilmDetail = apiRepository.getFilmDetail(header, filmId)
            emit(getFilmDetail)
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, e.message.toString())
        }
    }
}