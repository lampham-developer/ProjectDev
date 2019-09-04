package com.example.helloworld.Rss;

import org.jsoup.nodes.Element;

import java.util.List;

public class Article {
    private String time;
    private String title;
    private String description;
    List<Element> content;

    public Article() {
    }

    public Article(String time, String title, String description, List<Element> content) {
        this.time = time;
        this.title = title;
        this.description = description;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Element> getContent() {
        return content;
    }

    public void setContent(List<Element> content) {
        this.content = content;
    }
}
