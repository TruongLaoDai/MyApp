package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class MediaReaction {
    @SerializedName("amountLike")
    private long amountLike;
    @SerializedName("amountDislike")
    private long amountDislike;
    @SerializedName("reactionUser")
    private int reactionUser;

    public long getAmountLike() {
        return amountLike;
    }

    public void setAmountLike(long amountLike) {
        this.amountLike = amountLike;
    }

    public long getAmountDislike() {
        return amountDislike;
    }

    public void setAmountDislike(long amountDislike) {
        this.amountDislike = amountDislike;
    }

    public int getReactionUser() {
        return reactionUser;
    }

    public void setReactionUser(int reactionUser) {
        this.reactionUser = reactionUser;
    }
}
