package com.smile.watchmovie.api

class ApiHelper(private val apiService: ApiServices) {
    suspend fun getInfoWeather(cityName: String, units: String, appid: String) =
        apiService.getInfoWeather(cityName, units, appid)

    suspend fun searchFilm(header: String, keySearch: String, page: Int, size: Int) =
        apiService.searchFilm(header, keySearch, page, size)

    suspend fun getFilmDetail(header: String, filmId: Int) =
        apiService.getFilmDetail(header, filmId)

    suspend fun getFilmByCategory(header: String, categoryId: Int, page: Int, size: Int) =
        apiService.getFilmByCategory(header, categoryId, page, size)
}