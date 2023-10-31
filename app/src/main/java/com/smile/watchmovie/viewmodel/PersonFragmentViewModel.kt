package com.smile.watchmovie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.smile.watchmovie.repository.ApiRepository
import kotlinx.coroutines.Dispatchers

class PersonFragmentViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    fun getInfoWeather(cityName: String, units: String, appid: String) = liveData(Dispatchers.IO) {
        try {
            val weatherFromApi = apiRepository.getInfoWeather(cityName, units, appid)
            emit(weatherFromApi)
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, e.message.toString())
        }
    }
}