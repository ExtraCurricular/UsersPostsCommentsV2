package com.WebServices.PostService.models;

import java.util.Date;

public class PostDTO {
    private long id;
    private String title;
    private String body;
    private long userId;
    private WeatherRequest location;

    public WeatherRequest getWeatherRequest() {
        return location;
    }

    public void setWeatherRequest(WeatherRequest weatherRequest) {
        this.location = weatherRequest;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
