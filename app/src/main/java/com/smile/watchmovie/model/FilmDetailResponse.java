package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class FilmDetailResponse {
    @SerializedName("errorCode")
    private String errorCode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private FilmMainHome data = null;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FilmMainHome getData() {
        return data;
    }

    public void setData(FilmMainHome data) {
        this.data = data;
    }
}
