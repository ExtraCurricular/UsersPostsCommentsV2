package com.WebServices.PostService.models;

public class PostDTO implements IPost{
    private long id;
    private String title;
    private String body;
    private long userId;
    private String location;
    private Float temperature;

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

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public PostDTO(Post post, float temperature){
        id = post.getId();
        title = post.getTitle();
        body = post.getBody();
        userId = post.getUserId();
        location = post.getLocation();

        this.temperature = temperature;
    }

    public PostDTO(Post post){
        id = post.getId();
        title = post.getTitle();
        body = post.getBody();
        userId = post.getUserId();
        location = post.getLocation();
    }
}
