package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ChannelDetail {

    @SerializedName("data")
    @Expose
    private Channel data;
    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("desc")
    @Expose
    private String desc;

    public ChannelDetail() {
    }

    public ChannelDetail(Channel data, int code, String desc) {
        this.data = data;
        this.code = code;
        this.desc = desc;
    }

    public Channel getData() {
        return data;
    }

    public void setData(Channel data) {
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