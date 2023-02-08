package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FilmSubModel implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("videoId")
    @Expose
    private int videoId;
    @SerializedName("subVideoId")
    @Expose
    private int subVideoId;
    @SerializedName("default_")
    @Expose
    private int default_;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getSubVideoId() {
        return subVideoId;
    }

    public void setSubVideoId(int subVideoId) {
        this.subVideoId = subVideoId;
    }

    public int getDefault_() {
        return default_;
    }

    public void setDefault_(int default_) {
        this.default_ = default_;
    }
}
