package com.smile.watchmovie.model;

import java.util.ArrayList;

public class FilmFavorites {
    private ArrayList<FilmFavorite> listFilmFavorite;

    public FilmFavorites() {
    }

    public ArrayList<FilmFavorite> getFilmFavorites() {
        return listFilmFavorite;
    }

    public void setFilmFavorites(ArrayList<FilmFavorite> filmFavoritest) {
        this.listFilmFavorite = filmFavoritest;
    }
}
