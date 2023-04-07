package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class AppMediaDetail {

    @SerializedName("data")
    @Expose
    private Video data;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("desc")
    @Expose
    private String desc;

    public AppMediaDetail() {
    }

    public AppMediaDetail(Video data, int code, String desc) {
        this.data = data;
        this.code = code;
        this.desc = desc;
    }

    public Video getData() {
        return data;
    }

    public void setData(Video data) {
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