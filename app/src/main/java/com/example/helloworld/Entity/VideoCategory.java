package com.example.helloworld.Entity;

public class VideoCategory {
    String title, avt_url;

    public VideoCategory(String title, String avt_url) {
        this.title = title;
        this.avt_url = avt_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvt_url() {
        return avt_url;
    }

    public void setAvt_url(String avt_url) {
        this.avt_url = avt_url;
    }
}
