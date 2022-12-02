package com.smile.watchmovie.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieArrayResponse {

    @SerializedName("errorCode")
    private String errorCode;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<MovieMainHome> data = null;

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

    public List<MovieMainHome> getData() {
        return data;
    }

    public void setData(List<MovieMainHome> data) {
        this.data = data;
    }
}

