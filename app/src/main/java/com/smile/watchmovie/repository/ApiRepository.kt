package com.smile.watchmovie.repository

import com.smile.watchmovie.api.ApiHelper

class ApiRepository(private val apiHelper: ApiHelper) {
    suspend fun getInfoWeather(cityName: String, units: String, appid: String) =
        apiHelper.getInfoWeather(cityName, units, appid)

    suspend fun searchFilm(header: String, keySearch: String, page: Int, size: Int) =
        apiHelper.searchFilm(header, keySearch, page, size)
}