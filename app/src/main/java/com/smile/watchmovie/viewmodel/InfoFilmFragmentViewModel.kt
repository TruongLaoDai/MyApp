package com.smile.watchmovie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.smile.watchmovie.page.CategoryFilmPagingSource
import com.smile.watchmovie.repository.ApiRepository
import kotlinx.coroutines.Dispatchers

class InfoFilmFragmentViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    fun getFilmDetail(header: String, filmId: Int) = liveData(Dispatchers.IO) {
        try {
            val film = apiRepository.getFilmDetail(header, filmId)
            emit(film)
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, e.message.toString())
        }
    }
}