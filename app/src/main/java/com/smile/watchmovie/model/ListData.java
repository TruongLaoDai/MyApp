package com.smile.watchmovie.model;

import java.util.List;

public class ListData {

    private int type;
    private String category;
    private List<FilmMainHome> listMovie;

    public ListData(int type, String category, List<FilmMainHome> listMovie) {
        this.type = type;
        this.category = category;
        this.listMovie = listMovie;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<FilmMainHome> getListMovie() {
        return listMovie;
    }

    public void setListMovie(List<FilmMainHome> listMovie) {
        this.listMovie = listMovie;
    }
}
