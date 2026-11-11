package com.wetterapp.dto;

/**
 * DTO (Data Transfer Object) fuer einen Tag der 5-Tage-Vorhersage.
 * Enthalt die durchschnittliche Temperatur und Wetterbeschreibung fuer einen Tag.
 */
public class ForecastResponse {

    private String date;
    private double tempMin;
    private double tempMax;
    private String description;
    private String icon;

    public ForecastResponse() {
    }

    public ForecastResponse(String date, double tempMin, double tempMax, String description, String icon) {
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.description = description;
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
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
