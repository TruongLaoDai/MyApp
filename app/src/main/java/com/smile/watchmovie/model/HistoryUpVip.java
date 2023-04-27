package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class HistoryUpVip {
    @SerializedName("is_vip")
    private String is_vip;
    @SerializedName("exp_vip")
    private String dateRegister;

    public HistoryUpVip() {
    }

    public HistoryUpVip(String is_vip, String dateRegister) {
        this.is_vip = is_vip;
        this.dateRegister = dateRegister;
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
