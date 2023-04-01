package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Channel {

    @SerializedName("id")
    @Expose
    private int id;
    //tên channel
    @SerializedName("channelName")
    @Expose
    private String channelName;
    //ảnh bìa channel
    @SerializedName("channelAvatar")
    @Expose
    private String channelAvatar;
    //tiêu đề của channel
    @SerializedName("headerBanner")
    @Expose
    private String headerBanner;
    //miêu tả channel
    @SerializedName("description")
    @Expose
    private String description;
    //số theo dõi
    @SerializedName("numFollows")
    @Expose
    private int numFollows;
    //số video
    @SerializedName("numVideos")
    @Expose
    private int numVideos;
    //chính thức
    @SerializedName("isOfficial")
    @Expose
    private int isOfficial;

    @SerializedName("createdFrom")
    @Expose
    private String createdFrom;
    // trạng thái theo dõi
    @SerializedName("isFollow")
    @Expose
    private int isFollow;
    // chính chủ
    @SerializedName("isOwner")
    @Expose
    private int isOwner;
    //link đến channel
    @SerializedName("url")
    @Expose
    private String url;
    //quốc gia
    @SerializedName("state")
    @Expose
    private String state;

    public Channel() {
    }

    public Channel(int id, String channelName, String channelAvatar, String headerBanner, String description, int numFollows, int numVideos, int isOfficial, String createdFrom, int isFollow, int isOwner, String url, String state) {
        this.id = id;
        this.channelName = channelName;
        this.channelAvatar = channelAvatar;
        this.headerBanner = headerBanner;
        this.description = description;
        this.numFollows = numFollows;
        this.numVideos = numVideos;
        this.isOfficial = isOfficial;
        this.createdFrom = createdFrom;
        this.isFollow = isFollow;
        this.isOwner = isOwner;
        this.url = url;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelAvatar() {
        return channelAvatar;
    }

    public void setChannelAvatar(String channelAvatar) {
        this.channelAvatar = channelAvatar;
    }

    public String getHeaderBanner() {
        return headerBanner;
    }

    public void setHeaderBanner(String headerBanner) {
        this.headerBanner = headerBanner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumFollows() {
        return numFollows;
    }

    public void setNumFollows(int numFollows) {
        this.numFollows = numFollows;
    }

    public int getNumVideos() {
        return numVideos;
    }

    public void setNumVideos(int numVideos) {
        this.numVideos = numVideos;
    }

    public int getOfficial() {
        return isOfficial;
    }

    public void setOfficial(int official) {
        isOfficial = official;
    }

    public String getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(String createdFrom) {
        this.createdFrom = createdFrom;
    }

    public int getFollow() {
        return isFollow;
    }

    public void setFollow(int follow) {
        isFollow = follow;
    }

    public int getOwner() {
        return isOwner;
    }

    public void setOwner(int owner) {
        isOwner = owner;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
