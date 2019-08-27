package com.example.helloworld.Entity;

import java.io.Serializable;

public class Article implements Serializable {
    private String title;
    private String thumb;
    private String link;
    private String des;

    public Article(String title, String thumb, String link, String des) {
        this.title = title;
        this.thumb = thumb;
        this.link = link;
        this.des = des;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
