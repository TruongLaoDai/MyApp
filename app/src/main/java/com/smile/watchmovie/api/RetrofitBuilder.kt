package com.smile.watchmovie.api

import com.smile.watchmovie.utils.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    private fun getRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiServiceWeather: ApiServices =
        getRetrofit(Constant.Api.BASE_URL_WEATHER).create(ApiServices::class.java)

    val apiServiceFilm: ApiServices =
        getRetrofit(Constant.Api.BASE_URL_FILM).create(ApiServices::class.java)
}