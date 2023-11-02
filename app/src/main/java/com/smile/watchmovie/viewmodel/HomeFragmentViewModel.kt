package com.smile.watchmovie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.smile.watchmovie.repository.ApiRepository
import kotlinx.coroutines.Dispatchers

class HomeFragmentViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    fun getFilmByCategory(header: String, categoryId: Int, page: Int, size: Int) =
        liveData(Dispatchers.IO) {
            try {
                val listFilm = apiRepository.getFilmByCategory(header, categoryId, page, size)
                emit(listFilm)
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, e.message.toString())
            }
        }

    fun getFilmDetail(header: String, filmId: Int) = liveData(Dispatchers.IO) {
        try {
            val film = apiRepository.getFilmDetail(header, filmId)
            emit(film)
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, e.message.toString())
        }
    }
}