package com.smile.watchmovie.api;

import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.FilmDetailResponse;
import com.smile.watchmovie.model.HistoryUpVipResponse;
import com.smile.watchmovie.model.MediaReactionResponse;
import com.smile.watchmovie.model.User;
import com.smile.watchmovie.model.UserResponse;
import com.smile.watchmovie.model.UserResponseLogin;
import com.smile.watchmovie.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //http://cinema.tl/api/v1/get-home-films?msisdn=094555566&page=3&size=10
    //http://cinema.tl/api/v1/search-films?keySearch=esi
    //http://cinema.tl/api/v1/get-film-detail?filmId=105
    //http://cinema.tl/api/v1/get-films-by-category?msisdn=094555566&page=0&size=10&categoryId=13
    ApiService apiService = ApiConfig.getClient("http://cinema.tl/")
            .create(ApiService.class);
    ApiService apiWeather = ApiConfig.getClient("https://api.openweathermap.org/")
            .create(ApiService.class);
    ApiService apiUser = ApiConfig.getClient("http://giangndt428.pythonanywhere.com/")
            .create(ApiService.class);

    @GET("api/v1/get-home-films")
    Call<FilmArrayResponse> getDataHomeFilms(@Header("wsToken") String header,
                                             @Query("msisdn") String msisdn,
                                             @Query("page") int page);

    @GET("api/v1/search-films")
    Call<FilmArrayResponse> searchFilms(@Header("wsToken") String header,
                                        @Query("keySearch") String keySearch,
                                        @Query("page") int page,
                                        @Query("size") int size);

    @GET("api/v1/get-film-detail")
    Call<FilmDetailResponse> getFilmDetail(@Header("wsToken") String header,
                                           @Query("filmId") int filmId);

    @GET("api/v1/get-films-by-category")
    Call<FilmArrayResponse> getFilmByCategory(@Header("wsToken") String header,
                                              @Query("categoryId") int categoryId,
                                              @Query("page") int page,
                                              @Query("size") int size);

    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeather(@Query("q") String cityName,
                                     @Query("units") String units,
                                     @Query("appid") String appid);

    @POST("api/user/login")
    Call<UserResponseLogin> loginUser(@Query("username") String username,
                                      @Query("password") String password);

    @POST("api/user/register_user")
    Call<UserResponse> registerUser(@Query("username") String username,
                                    @Query("password") String password,
                                    @Query("full_name") String full_name);

    @PUT("api/user/update_vip_user")
    Call<UserResponse> updateUserToVip(@Query("id_user") int id_user,
                                       @Query("is_vip") String is_vip);

    @PUT("api/user/update_user/{id_user}")
    Call<UserResponse> updateUser(@Path("id_user") String id_user,
                                  @Query("full_name") String full_name,
                                  @Query("address") String address,
                                  @Query("phone") String phone,
                                  @Query("gender") String gender);

    @GET("api/user/userinfo/{id_user}")
    Call<UserResponseLogin> getUser(@Path("id_user") String id_user);

    @GET("api/history/getlistHistorybyuserid/{id_user}")
    Call<HistoryUpVipResponse> getListHistoryToVip(@Path("id_user") String id_user);

    @POST("api/mediareaction/createMediaReaction")
    Call<UserResponse> upMediaReaction(@Query("id_user") String id_user,
                                       @Query("id_media") String id_media,
                                       @Query("type_media") String type_media,
                                       @Query("type_reaction") String type_reaction,
                                       @Query("type_favorite") String type_favorite);

    @GET("api/mediareaction/getMediaReaction")
    Call<MediaReactionResponse> getMediaReaction(@Query("id_user") String id_user,
                                                 @Query("id_media") String id_media);

    @PUT("api/mediareaction/updateMediaReaction")
    Call<UserResponse> updateMediaReaction(@Query("id_mreaction") int id_mreaction,
                                   @Query("type_favorite") String type_favorite,
                                   @Query("type_reaction") String type_reaction);

    @GET("api/mediareaction/getlistmediafavorite/{id_user}")
    Call<User> getListMediaFavorite(@Path("id_user") int id_user);

    @POST("api/historymedia/createhistorymedia")
    Call<UserResponse> upHistoryMedia(@Query("id_media") String id_media,
                                      @Query("id_user") int id_user,
                                      @Query("type_media") String type_media,
                                      @Query("duration") long duration);

    @GET("api/historymedia/getlisthistorybyuserid/{id_user}")
    Call<User> getListHistoryMedia(@Path("id_user") int id_user);

    @PUT("api/historymedia/updatehistorymedia")
    Call<User> updateHistoryMedia(@Query("id_media") int id_media,
                                  @Query("id_user") int id_user,
                                  @Query("duration") long duration);
}
