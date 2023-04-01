package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Cate {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("cateName")
    @Expose
    private String cateName;
    @SerializedName("iconImage")
    @Expose
    private String iconImage;
    @SerializedName("headerBanner")
    @Expose
    private String headerBanner;
    @SerializedName("description")
    @Expose
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
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

}
