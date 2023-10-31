package com.smile.watchmovie.api;

import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmDetailResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    ApiService apiService = ApiConfig.getClient("http://cinema.tl/").create(ApiService.class);

    @GET("api/v1/get-film-detail")
    Call<FilmDetailResponse> getFilmDetail(@Header("wsToken") String header,
                                           @Query("filmId") int filmId);

    @GET("api/v1/get-films-by-category")
    Call<FilmArrayResponse> getFilmByCategory(@Header("wsToken") String header,
                                              @Query("categoryId") int categoryId,
                                              @Query("page") int page,
                                              @Query("size") int size);
}
