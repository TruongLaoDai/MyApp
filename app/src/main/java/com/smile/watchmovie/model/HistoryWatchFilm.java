package com.smile.watchmovie.model;

import com.google.firebase.firestore.Exclude;

public class HistoryWatchFilm {
    private int id_film;
    private long duration;
    private String dayWatch;
    private String documentID;
    private String avatarFilm;
    private String nameFilm;

    public HistoryWatchFilm() {
    }
    public HistoryWatchFilm(int id_film, long duration, String dayWatch, String avatarFilm, String nameFilm) {
        this.id_film = id_film;
        this.duration = duration;
        this.dayWatch = dayWatch;
        this.avatarFilm = avatarFilm;
        this.nameFilm = nameFilm;
    }

    public int getId_film() {
        return id_film;
    }

    public void setId_film(int id_film) {
        this.id_film = id_film;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDayWatch() {
        return dayWatch;
    }

    public void setDayWatch(String dayWatch) {
        this.dayWatch = dayWatch;
    }

    public String getAvatarFilm() {
        return avatarFilm;
    }

    public void setAvatarFilm(String avatarFilm) {
        this.avatarFilm = avatarFilm;
    }

    public String getNameFilm() {
        return nameFilm;
    }

    public void setNameFilm(String nameFilm) {
        this.nameFilm = nameFilm;
    }
}
