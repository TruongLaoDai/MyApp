package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class FilmMainHome implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("categoryId")
    @Expose
    private int categoryId;
    @SerializedName("star")
    @Expose
    private int star;
    @SerializedName("imdbPoint")
    @Expose
    private int imdbPoint;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isActive")
    @Expose
    private int isActive;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modifier")
    @Expose
    private String modifier;
    @SerializedName("modifiedDate")
    @Expose
    private Date modifiedDate;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("isSlide")
    @Expose
    private int isSlide;
    @SerializedName("isHome")
    @Expose
    private int isHome;
    @SerializedName("isHot")
    @Expose
    private int isHot;
    @SerializedName("isSeri")
    @Expose
    private int isSeri;
    @SerializedName("isCinema")
    @Expose
    private int isCinema;
    @SerializedName("isPopular")
    @Expose
    private boolean isPopular;
    @SerializedName("isFree")
    @Expose
    private int isFree;
    @SerializedName("viewNumber")
    @Expose
    private int viewNumber;
    @SerializedName("subCategoryId")
    @Expose
    private String subCategoryId;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("order_")
    @Expose
    private Long order;
    @SerializedName("nameKey")
    @Expose
    private String nameKey;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("descriptionEx")
    @Expose
    private String descriptionEx;
    @SerializedName("videoTrailer")
    @Expose
    private String videoTrailer;
    @SerializedName("poster")
    @Expose
    private String poster;
    @SerializedName("quality")
    @Expose
    private String quality;
    @SerializedName("autoOn")
    @Expose
    private int autoOn;
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("episodesTotal")
    @Expose
    private int episodesTotal;
    @SerializedName("subVideoList")
    @Expose
    private List<SubFilm> subVideoList;
    @SerializedName("subTitleList")
    @Expose
    private List<FilmSubModel> subTitleList;
    @SerializedName("redirectLink")
    @Expose
    private String redirectLink;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getImdbPoint() {
        return imdbPoint;
    }

    public void setImdbPoint(int imdbPoint) {
        this.imdbPoint = imdbPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getIsSlide() {
        return isSlide;
    }

    public void setIsSlide(int isSlide) {
        this.isSlide = isSlide;
    }

    public int getIsHome() {
        return isHome;
    }

    public void setIsHome(int isHome) {
        this.isHome = isHome;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public int getIsSeri() {
        return isSeri;
    }

    public void setIsSeri(int isSeri) {
        this.isSeri = isSeri;
    }

    public int getIsCinema() {
        return isCinema;
    }

    public void setIsCinema(int isCinema) {
        this.isCinema = isCinema;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionEx() {
        return descriptionEx;
    }

    public void setDescriptionEx(String descriptionEx) {
        this.descriptionEx = descriptionEx;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getAutoOn() {
        return autoOn;
    }

    public void setAutoOn(int autoOn) {
        this.autoOn = autoOn;
    }

    public int getEpisodesTotal() {
        return episodesTotal;
    }

    public void setEpisodesTotal(int episodesTotal) {
        this.episodesTotal = episodesTotal;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getVideoTrailer() {
        return videoTrailer;
    }

    public void setVideoTrailer(String videoTrailer) {
        this.videoTrailer = videoTrailer;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public List<SubFilm> getSubVideoList() {
        return subVideoList;
    }

    public void setSubVideoList(List<SubFilm> subVideoList) {
        this.subVideoList = subVideoList;
    }

    public List<FilmSubModel> getSubTitleList() {
        return subTitleList;
    }

    public void setSubTitleList(List<FilmSubModel> subTitleList) {
        this.subTitleList = subTitleList;
    }
}
