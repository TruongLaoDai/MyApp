package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
