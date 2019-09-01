package com.example.helloworld.Rss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.helloworld.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    TextView tv_news_time, tv_news_title, tv_news_des;
    LinearLayout layout_news_normal;
    Toolbar toolbar;
    String url;
    Document document;
    Article article;
    LinearLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = findViewById(R.id.toolbar_news);
        tv_news_time = findViewById(R.id.tv_news_time);
        tv_news_title = findViewById(R.id.tv_news_title);
        tv_news_des = findViewById(R.id.tv_news_des);
        layout_news_normal = findViewById(R.id.layout_news_normal);

        params = (LinearLayout.LayoutParams) layout_news_normal.getLayoutParams();

        if (getIntent().getStringExtra(getString(R.string.news_url)) != null) {
            url = getIntent().getStringExtra(getString(R.string.news_url));
        }

        new getHTTPData().execute();
    }


    private void getData(String news_url) {
        try {
            document = Jsoup.connect(news_url).get();
            article = null;
            String time = null, title = null, des = null;
            List<String> content = null;
            if (document != null) {
                Element itemList = document.getElementsByClass("sidebar_1").first();
                if (itemList != null) {
                    Element timeSubject = itemList.getElementsByClass("time left").first();
                    Element titleSubject = itemList.getElementsByClass("title_news_detail mb10").first();
                    Element desSubject = itemList.getElementsByClass("description").first();
                    Elements contentSubject = itemList.getElementsByClass("Normal");


                    if (timeSubject != null) {
                        time = timeSubject.text();
                    }
                    if (titleSubject != null) {
                        title = titleSubject.text();
                    }
                    if (desSubject != null) {
                        des = desSubject.text();
                    }

                    if (contentSubject!= null){
                        content = new ArrayList<>();
                        for (Element e : contentSubject) {
                            content.add(e.text());
                        }
                    }

                    article = new Article(time, title, des, content);
                }
            }

        } catch (Exception e) {

        }
    }

    private void addContent(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize((float)20);
        textView.setLayoutParams(params);
        layout_news_normal.addView(textView);
    }

    private class getHTTPData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getData(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (article != null) {
                if (article.getTime() != null) {
                    tv_news_time.setText(article.getTime());
                }
                if (article.getTitle() != null) {
                    tv_news_title.setText(article.getTitle());
                }
                if (article.getDescription() != null) {
                    tv_news_des.setText(article.getDescription());
                }
                if (article.getContent() != null){
                    for (String text: article.getContent()) {
                        addContent(text);
                    }
                }
            }
        }
    }
}
