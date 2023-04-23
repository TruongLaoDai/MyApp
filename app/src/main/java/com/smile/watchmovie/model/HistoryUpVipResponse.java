package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryUpVipResponse {

    @SerializedName("code")
    private String code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<HistoryUpVip> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HistoryUpVip> getData() {
        return data;
    }

    public void setData(List<HistoryUpVip> data) {
        this.data = data;
    }
}
