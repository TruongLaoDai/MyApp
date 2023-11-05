package com.smile.watchmovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.smile.watchmovie.page.CategoryFilmPagingSource
import com.smile.watchmovie.repository.ApiRepository

class IntroduceFilmViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    fun getListFilmRelate(categoryId: Int) = Pager(PagingConfig(pageSize = 10)) {
        CategoryFilmPagingSource(apiRepository, categoryId)
    }.flow.cachedIn(viewModelScope)
}