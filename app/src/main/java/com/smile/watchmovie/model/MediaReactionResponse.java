package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class MediaReactionResponse {

    @SerializedName("id_mreaction")
    private int id_mreaction;
    @SerializedName("data")
    private MediaReaction mediaReaction;
    @SerializedName("favorite")
    private int favorite;

    public int getId_mreaction() {
        return id_mreaction;
    }

    public void setId_mreaction(int id_mreaction) {
        this.id_mreaction = id_mreaction;
    }

    public MediaReaction getMediaReaction() {
        return mediaReaction;
    }

    public void setMediaReaction(MediaReaction mediaReaction) {
        this.mediaReaction = mediaReaction;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
