package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class AppMedia {

    @SerializedName("data")
    @Expose
    private List<Video> data;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("desc")
    @Expose
    private String desc;

    public AppMedia() {
    }

    public AppMedia(List<Video> data, int code, String desc) {
        this.data = data;
        this.code = code;
        this.desc = desc;
    }

    public List<Video> getData() {
        return data;
    }

    public void setAddVideo(List<Video> list){this.data.addAll(list); }

    public void setData(List<Video> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}