package com.example.helloworld.Rss;

import java.io.Serializable;

public class RssObject implements Serializable {
    private String title;
    private String link;
    private String thumb;
    private String des;
    private String date;

    public RssObject(String title, String link, String thumb, String des, String date) {
        this.title = title;
        this.link = link;
        this.thumb = thumb;
        this.des = des;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
