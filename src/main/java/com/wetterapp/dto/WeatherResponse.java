package com.wetterapp.dto;

/**
 * DTO (Data Transfer Object) fuer Wetterdaten von OpenWeatherMap.
 * Entspricht der JSON-Antwort der API.
 */
public class WeatherResponse {

    private String city;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String description;
    private String icon;

    public WeatherResponse() {
    }

    public WeatherResponse(String city, double temperature, double feelsLike,
                           int humidity, double windSpeed, String description, String icon) {
        this.city = city;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
        this.icon = icon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
