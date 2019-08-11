package com.example.helloworld.Entity;

import java.io.Serializable;

public class Video implements Serializable {

    private String title;
    private String date_public;
    private String artis_name;
    private String avt_url;
    private String mp4_url;


    public Video(String title, String date_public, String artis_name, String avt_url, String mp4_url) {
        this.title = title;
        this.date_public = date_public;
        this.artis_name = artis_name;
        this.avt_url = avt_url;
        this.mp4_url = mp4_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_public() {
        return date_public;
    }

    public void setDate_public(String date_public) {
        this.date_public = date_public;
    }

    public String getArtis_name() {
        return artis_name;
    }

    public void setArtis_name(String artis_name) {
        this.artis_name = artis_name;
    }

    public String getAvt_url() {
        return avt_url;
    }

    public void setAvt_url(String avt_url) {
        this.avt_url = avt_url;
    }

    public String getMp4_url() {
        return mp4_url;
    }

    public void setMp4_url(String mp4_url) {
        this.mp4_url = mp4_url;
    }
}
