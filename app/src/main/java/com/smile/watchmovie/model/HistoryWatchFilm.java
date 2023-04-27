package com.smile.watchmovie.model;

import com.google.firebase.firestore.Exclude;

public class HistoryWatchFilm {
    private int id_film;
    private long duration;
    private String dayWatch;
    private String documentID;

    public HistoryWatchFilm() {
    }

    public HistoryWatchFilm(int id_film, long duration, String dayWatch) {
        this.id_film = id_film;
        this.duration = duration;
        this.dayWatch = dayWatch;
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
}
