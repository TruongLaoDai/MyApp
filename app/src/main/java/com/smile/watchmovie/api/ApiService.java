package com.smile.watchmovie.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smile.watchmovie.model.MovieArrayResponse;
import com.smile.watchmovie.model.MovieDetailResponse;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    //http://cinema.tl/api/v1/get-home-films?msisdn=094555566&page=3&size=10
    //http://cinema.tl/api/v1/search-films?keySearch=esi
    //http://cinema.tl/api/v1/get-film-detail?filmId=105
    ApiService apiService = ApiConfig.getClient("http://cinema.tl/")
            .create(ApiService.class);

    @GET("api/v1/get-home-films")
    Call<MovieArrayResponse> getDataHomeFilms(@Header("wsToken") String header,
                                              @Query("msisdn") String msisdn,
                                              @Query("page") int page);

    @GET("api/v1/search-films")
    Call<MovieArrayResponse> searchFilms(@Header("wsToken") String header,
                                         @Query("keySearch") String keySearch,
                                         @Query("page") int page,
                                         @Query("size") int size);

    @GET("api/v1/get-film-detail")
    Call<MovieDetailResponse> getFilmDetail(@Header("wsToken") String header,
                                            @Query("filmId") int filmId);

}
