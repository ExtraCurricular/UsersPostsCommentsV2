package com.WebServices.PostService.models;

import java.util.Date;

public class PostDTO {
    private long id;
    private String title;
    private String body;
    private long userId;
    private String location;
    private Float temperature;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public PostDTO(Post post, float temperature){
        id = post.getId();
        title = post.getTitle();
        body = post.getBody();
        userId = post.getUserId();
        location = post.getLocation();
        date = post.getDate();

        this.temperature = temperature;
    }

    public PostDTO(Post post){
        id = post.getId();
        title = post.getTitle();
        body = post.getBody();
        userId = post.getUserId();
        location = post.getLocation();
        date = post.getDate();
    }
}
