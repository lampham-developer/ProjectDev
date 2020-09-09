package com.example.helloworld.Rss.RssReader;

import com.example.helloworld.R;
import com.example.helloworld.Rss.RssObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChungTa_RSS_Reader {
    public void getMenuList(String url, Document document, List<RssObject> objectList) {
        RssObject rssObject;
        try {
            document = Jsoup.connect(url).get();
            if (document != null) {
                Element menulist = document.getElementsByClass("main-nav").get(0);

                Elements list_title = menulist.getElementsByTag("a");
                for (Element e : list_title) {
                    String link = cutLink(e.attr("href"));
                    if(link == "" || link.isEmpty()) continue;
                    String title = e.attr("title");
                }
            }
            objectList.remove(objectList.size() - 1);
            objectList.remove(objectList.size() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getArticleList(String url, Document document, List<RssObject> objectList) {
        RssObject rssObject;
        objectList = new ArrayList<>();
        String link = null, title = null, des = null, thumb = null;
        try {
            document = Jsoup.connect(url).get();
            if (document != null) {
                Elements itemList = document.getElementsByClass("sidebar_1").first().getElementsByClass("list_news");

                for (Element e : itemList) {
                    Element titleSubject = e.getElementsByClass("title_news").first();
                    Element desSubject = e.getElementsByClass("description").first();
                    Element thumbSubject = e.getElementsByClass("thumb_art").first();

                    if (titleSubject != null) {
                        link = titleSubject.getElementsByTag("a").first().attr("href");
                        title = titleSubject.getElementsByTag("a").first().attr("title");
                    }
                    if (desSubject != null) {
                        des = e.getElementsByClass("description").text();
                    }
                    if (thumbSubject != null) {
                        thumb = thumbSubject.getElementsByTag("a").first().getElementsByTag("img").attr("data-original");
                    } else thumb = null;


                    rssObject = new RssObject(title, link, thumb, des, "");
                    objectList.add(rssObject);

                }
            }
        } catch (IOException e) {
        }
    }

    private String cutLink(String s) {
        int index = 0;
        for (int count = s.length(); count >= 0; count--) {
            try {
                if (s.charAt(count) == '/') {
                    index = count;
                    break;
                }
            } catch (Exception e) {

            }
        }
        return s.substring(index + 1);
    }
}
