package com.smile.watchmovie.model;

import java.util.ArrayList;

public class HistoryFilm {
    private int idFilm;
    private Long position;

    public HistoryFilm() {
    }

    public int getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
}
