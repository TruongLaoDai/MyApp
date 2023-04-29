package com.smile.watchmovie.model;

import com.google.firebase.firestore.Exclude;

public class FilmReaction {
    private String documentId;
    private String idUser;
    private String avatarFilm;
    private String nameFilm;
    private String dateReact;
    private int type_reaction;
    private int idFilm;

    public FilmReaction() {
    }

    public FilmReaction(String idUser, String dateReact, int type_reaction) {
        this.idUser = idUser;
        this.dateReact = dateReact;
        this.type_reaction = type_reaction;
    }

    public FilmReaction(int idFilm, String avatarFilm, String nameFilm, String dateReact, int type_reaction) {
        this.idFilm = idFilm;
        this.avatarFilm = avatarFilm;
        this.nameFilm = nameFilm;
        this.dateReact = dateReact;
        this.type_reaction = type_reaction;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDateReact() {
        return dateReact;
    }

    public void setDateReact(String dateReact) {
        this.dateReact = dateReact;
    }

    public int getType_reaction() {
        return type_reaction;
    }

    public void setType_reaction(int type_reaction) {
        this.type_reaction = type_reaction;
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