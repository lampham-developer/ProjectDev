package com.example.helloworld.News.RssReader;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.News.Activity.NewsActivity;
import com.example.helloworld.News.Adapter.SuggestArticleAdapter;
import com.example.helloworld.News.Entities.Article;
import com.example.helloworld.News.Entities.IRssItemClick;
import com.example.helloworld.News.Entities.RssObject;
import com.example.helloworld.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChungTa_RSS_Reader {
    public List<RssObject> getMenuList(Document document, List<RssObject> objectList) {
        RssObject rssObject;

                Element menulist = document.getElementsByClass("divblock menu_div").get(0);

                Elements list_title = menulist.getElementsByTag("a");
                for (Element e : list_title) {
                    String link = cutLink(e.attr("href"));
                    if(link == "" || link.isEmpty()) continue;
                    String title = e.attr("title");
                    rssObject = new RssObject();
                    rssObject.setLink(link);
                    rssObject.setTitle(title);

                    objectList.add(rssObject);
                }
        objectList.remove(objectList.size()-1);
        return objectList;
    }

    public List<RssObject> getArticleList(Document document, List<RssObject> objectList) {
        RssObject rssObject;
        objectList = new ArrayList<>();
        String link = null, title = null, des = null, thumb = null;

                Elements itemList = document.getElementsByClass("ul-folder").first().getElementsByTag("li");

                for (Element e : itemList) {
                    Element titleSubject = e.getElementsByClass("title").first();
                    Element desSubject = e.getElementsByClass("lead").first();
                    Element thumbSubject = e.getElementsByClass("thumb-art").first();

                    if (titleSubject != null) {
                        link = Define.SERVER_URL + cutLink(titleSubject.getElementsByTag("a").first().attr("href"));
                        title = titleSubject.getElementsByTag("a").first().attr("title");
                    }
                    if (desSubject != null) {
                        des = e.getElementsByClass("lead").text();
                    }
                    if (thumbSubject != null) {
                        thumb = thumbSubject.getElementsByTag("a").first().getElementsByTag("img").attr("src");
                    } else thumb = null;


                    rssObject = new RssObject(title, link, thumb, des, "");
                    objectList.add(rssObject);

                }
        return objectList;
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
