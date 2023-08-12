package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FilmArrayResponse {
    @SerializedName("errorCode")
    private String errorCode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<FilmMainHome> data = null;

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

    public List<FilmMainHome> getData() {
        return data;
    }

    public void setData(List<FilmMainHome> data) {
        this.data = data;
    }
}

