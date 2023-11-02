package com.smile.watchmovie.api

import com.smile.watchmovie.model.FilmArrayResponse
import com.smile.watchmovie.model.FilmDetailResponse
import com.smile.watchmovie.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiServices {
    @GET("data/2.5/weather")
    suspend fun getInfoWeather(
        @Query("q") cityName: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ): WeatherResponse

    @GET("api/v1/search-films")
    suspend fun searchFilm(
        @Header("wsToken") header: String,
        @Query("keySearch") keySearch: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): FilmArrayResponse

    @GET("api/v1/get-film-detail")
    suspend fun getFilmDetail(
        @Header("wsToken") header: String,
        @Query("filmId") filmId: Int
    ): FilmDetailResponse

    @GET("api/v1/get-films-by-category")
    suspend fun getFilmByCategory(
        @Header("wsToken") header: String,
        @Query("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): FilmArrayResponse
}