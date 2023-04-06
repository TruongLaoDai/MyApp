package com.smile.watchmovie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("weather")
    private List<Weather>  weather;
    @SerializedName("main")
    private TemperatureHumidity main;

    public TemperatureHumidity getMain() {
        return main;
    }

    public void setMain(TemperatureHumidity main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
