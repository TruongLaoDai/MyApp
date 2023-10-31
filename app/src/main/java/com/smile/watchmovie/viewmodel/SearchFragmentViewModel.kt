package com.smile.watchmovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.smile.watchmovie.page.PagingSource
import com.smile.watchmovie.repository.ApiRepository

class SearchFragmentViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    fun getListFilm(keySearch: String) = Pager(PagingConfig(pageSize = 10)) {
        PagingSource(apiRepository, keySearch)
    }.flow.cachedIn(viewModelScope)
}