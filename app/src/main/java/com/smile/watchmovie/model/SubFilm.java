package com.smile.watchmovie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SubFilm implements Serializable, Comparable<SubFilm>{

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("videoId")
    @Expose
    private int videoId;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("isActive")
    @Expose
    private int isActive;
    @SerializedName("episode")
    @Expose
    private int episode;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("autoOn")
    @Expose
    private String autoOn;
    @SerializedName("schedule")
    @Expose
    private String schedule;
    @SerializedName("subTitleList")
    @Expose
    private List<FilmSubModel> subTitleList = null;
    @SerializedName("redirectLink")
    @Expose
    private String redirectLink;

    private boolean isWatching;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getAutoOn() {
        return autoOn;
    }

    public void setAutoOn(String autoOn) {
        this.autoOn = autoOn;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public List<FilmSubModel> getSubTitleList() {
        return subTitleList;
    }

    public void setSubTitleList(List<FilmSubModel> subTitleList) {
        this.subTitleList = subTitleList;
    }

    public void setWatching(boolean isWatching){
        this.isWatching = isWatching;
    }

    public boolean getIsWatching(){ return isWatching;}
    @Override
    public int compareTo(SubFilm o) {
        return this.getEpisode() > o.episode ? 1 : -1 ;
    }
}
