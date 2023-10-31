package com.smile.watchmovie.model;

import java.util.List;

public class WeatherResponse {
    private List<Weather> weather;
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
