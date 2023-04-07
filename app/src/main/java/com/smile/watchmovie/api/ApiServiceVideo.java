package com.smile.watchmovie.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smile.watchmovie.model.AppMedia;
import com.smile.watchmovie.model.AppMediaDetail;
import com.smile.watchmovie.model.ChannelDetail;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServiceVideo {

    //http://freeapi.kakoak.tls.tl/video-service/v1/video/hot?msisdn=0969633777&timestamp=123&security=123&page=0&size=10&lastHashId=13asd

    //http://freeapi.kakoak.tls.tl/video-service/v1/video/165457/info?msisdn=0966409095&timestamp=123&security=123
    //http://videoapi.kakoak.tls.tl/video-service/v1/video/search/v2?msisdn=0969633777&timestamp=123&security=123&page=1&size=10&q=ha&lastHashId=
    ApiServiceVideo apiServiceVideo = ApiConfig.getClient("http://videoapi.kakoak.tls.tl")
            .create(ApiServiceVideo.class);

    @GET("/video-service/v1/video/hot")
    Call<AppMedia> getdatatohome(@Header("Accept-Language") String header,
                                 @Query("msisdn") String msisdn,
                                 @Query("timestamp") int timestamp,
                                 @Query("security") String security,
                                 @Query("page") int page,
                                 @Query("size") int size,
                                 @Query("lastHashId") String lastHashId);

    @GET("/video-service/v1/video/{id}/info")
    Call<AppMediaDetail> getdatatodetail(@Header("Accept-Language") String header,
                                         @Path("id") int id,
                                         @Query("msisdn") String msisdn,
                                         @Query("timestamp") int timestamp,
                                         @Query("security") String security);

    @GET("/video-service/v1/video/search/v2")
    Call<AppMedia> getdatatosearch(@Header("Accept-Language") String header,
                                   @Query("msisdn") String msisdn,
                                   @Query("timestamp") int timestamp,
                                   @Query("security") String security,
                                   @Query("page") int page,
                                   @Query("size") int size,
                                   @Query("q") String q,
                                   @Query("lastHashId") String lastHashId);

    //http://videoapi.kakoak.tls.tl/video-service/v1/video/6097/related?msisdn=0969633777&timestamp=123&security=123&page=0&size=10&lastHashId=13asd
    @GET("/video-service/v1/video/{id}/related")
    Call<AppMedia> getVideoRelated(@Path("id") int id,
                                   @Query("msisdn") String msisdn,
                                   @Query("timestamp") String timestamp,
                                   @Query("security") String security,
                                   @Query("page") int page,
                                   @Query("size") int size,
                                   @Query("lastHashId") String lastHashId,
                                   @Header("Accept-language") String header);

    @GET("/video-service/v1/video/hot")
    Call<AppMedia> getHomeListVideoHot(@Query("msisdn") String msisdn,
                                       @Query("timestamp") String timestamp,
                                       @Query("security") String security,
                                       @Query("page") int page,
                                       @Query("size") int size,
                                       @Query("lastHashId") String lastHashId,
                                       @Header("Accept-language") String acceptLanguage,
                                       @Header("mocha-api") String mochaApi,
                                       @Header("sec-api") String sec_api);

    // tìm channel theo id
    //http://videoapi.kakoak.tls.tl/video-service/v1/channel/328/info?msisdn=%2B67075615473&timestamp=1611796455960&security=&clientType=Android&revision=15511
    @GET("/video-service/v1/channel/{id}/info")
    Call<ChannelDetail> getChannelById(@Path("id") int id,
                                       @Header("Accept-language") String header,
                                       @Header("mocha-api") String mochaApi,
                                       @Query("msisdn") String msisdn,
                                       @Query("timestamp") String timestamp,
                                       @Query("security") String security,
                                       @Query("clientType") String clientType,
                                       @Query("revision") String revision);

    // list video của 1 channel
    //http://videoapi.kakoak.tls.tl/video-service/v1/video/10000/channel?msisdn=%2B67075600203&lastHashId=&page=0&size=20&timestamp=1611103216618&security=&clientType=Android&revision=15511
    @GET("/video-service/v1/video/{id}/channel")
    Call<AppMedia> getListVideoByChannelId(@Path("id") int id,
                                           @Header("Accept-language") String header,
                                           @Header("mocha-api") String mochaApi,
                                           @Query("msisdn") String msisdn,
                                           @Query("lastHashId") String lastHashId,
                                           @Query("page") int page,
                                           @Query("size") int size,
                                           @Query("timestamp") String timestamp,
                                           @Query("security") String security,
                                           @Query("clientType") String clientType,
                                           @Query("revision") String revision);

}