package com.smile.watchmovie.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smile.watchmovie.api.ApiHelper
import com.smile.watchmovie.repository.ApiRepository
import com.smile.watchmovie.viewmodel.CategoryFilmActivityViewModel
import com.smile.watchmovie.viewmodel.HomeFragmentViewModel
import com.smile.watchmovie.viewmodel.InfoFilmFragmentViewModel
import com.smile.watchmovie.viewmodel.PersonFragmentViewModel
import com.smile.watchmovie.viewmodel.SearchFragmentViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonFragmentViewModel::class.java)) {
            return PersonFragmentViewModel(ApiRepository(apiHelper)) as T
        }

        if (modelClass.isAssignableFrom(SearchFragmentViewModel::class.java)) {
            return SearchFragmentViewModel(ApiRepository(apiHelper)) as T
        }

        if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {
            return HomeFragmentViewModel(ApiRepository(apiHelper)) as T
        }

        if (modelClass.isAssignableFrom(CategoryFilmActivityViewModel::class.java)) {
            return CategoryFilmActivityViewModel(ApiRepository(apiHelper)) as T
        }

        if (modelClass.isAssignableFrom(InfoFilmFragmentViewModel::class.java)) {
            return InfoFilmFragmentViewModel(ApiRepository(apiHelper)) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}