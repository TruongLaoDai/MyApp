package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class HistoryUpVip {
    @SerializedName("id")
    private int id;
    @SerializedName("is_vip")
    private String is_vip;
    @SerializedName("exp_vip")
    private String dateRegister;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }
}
