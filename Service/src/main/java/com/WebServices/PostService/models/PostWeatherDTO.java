package com.WebServices.PostService.models;

import java.util.Date;

public class PostWeatherDTO {

    private Float temperature;
    private String city;
    private Date date;

    public PostWeatherDTO(String city, Date date, Float temperature) {
        this.city = city;
        this.date = date;
        this.temperature = temperature;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
