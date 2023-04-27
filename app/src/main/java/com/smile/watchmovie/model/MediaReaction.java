package com.smile.watchmovie.model;

import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.SerializedName;

public class MediaReaction {
    private String documentId;
    private String idUser;
    private String dateReact;
    private int type_reaction;

    public MediaReaction() {
    }

    public MediaReaction(String idUser, String dateReact, int type_reaction) {
        this.idUser = idUser;
        this.dateReact = dateReact;
        this.type_reaction = type_reaction;
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
}
