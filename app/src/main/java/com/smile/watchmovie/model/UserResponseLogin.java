package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class UserResponseLogin {
    @SerializedName("data")
    private User data;
    @SerializedName("message")
    private String message;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
