package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

public class MovieDetailResponse {
    @SerializedName("errorCode")
    private String errorCode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private MovieMainHome data = null;

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

    public MovieMainHome getData() {
        return data;
    }

    public void setData(MovieMainHome data) {
        this.data = data;
    }
}
