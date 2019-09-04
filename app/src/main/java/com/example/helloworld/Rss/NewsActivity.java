package com.example.helloworld.Rss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.helloworld.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    TextView tv_news_time, tv_news_title, tv_news_des;
    LinearLayout layout_news_normal;
    Toolbar toolbar;
    ActionBar actionbar;
    String url;
    Document document;
    Article article;
    LinearLayout.LayoutParams params;

    TextView tv_suggest_news;
    RecyclerView rv_suggest_news;
    RssObjectAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = findViewById(R.id.toolbar_news);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        tv_news_time = findViewById(R.id.tv_news_time);
        tv_news_title = findViewById(R.id.tv_news_title);
        tv_news_des = findViewById(R.id.tv_news_des);
        layout_news_normal = findViewById(R.id.layout_news_normal);

        tv_suggest_news = findViewById(R.id.tv_suggest_news);
        rv_suggest_news = findViewById(R.id.rv_suggest_news);


        params = (LinearLayout.LayoutParams) layout_news_normal.getLayoutParams();

        if (getIntent().getStringExtra(getString(R.string.news_url)) != null) {
            url = getIntent().getStringExtra(getString(R.string.news_url));
        }

        new getHTTPData().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getArticleData(String news_url) {
        try {
            document = Jsoup.connect(news_url).get();
            article = null;
            String time = null, title = null, des = null;
            List<Element> contentList = null;
            if (document != null) {
                Element itemList = document.getElementsByClass("sidebar_1").first();
                if (document != null) {
                    Element timeSubject = document.getElementsByClass("time left").first();
                    Element titleSubject = document.getElementsByClass("title_news_detail mb10").first();
                    Element desSubject = document.getElementsByClass("description").first();
                    Element normalSubject = document.getElementsByClass("content_detail fck_detail width_common block_ads_connect").first();

                    Element slideSubject = document.getElementsByClass("content_detail fck_detail width_common").first();

                    if (timeSubject != null) {
                        time = timeSubject.text();
                    }
                    if (titleSubject != null) {
                        title = titleSubject.text();
                    }
                    if (desSubject != null) {
                        des = desSubject.text();
                    }

                    if (normalSubject != null) {
                        contentList = new ArrayList<>();
                        for (Element e : normalSubject.getAllElements()) {
                            contentList.add(e);
                        }
                        contentList.remove(0);
                    }

                    if (!isNullElemt(slideSubject)) {
                        contentList = new ArrayList<>();
                        for (Element element : slideSubject.getElementsByClass("item_slide_show clearfix")) {
                            contentList.add(element);
                        }
                    }

                    article = new Article(time, title, des, contentList);
                }
            }

        } catch (Exception e) {

        }
    }

    private void setArticleData() {
        layout_news_normal.removeAllViews();
        tv_news_title.getParent().requestChildFocus(tv_news_title, tv_news_title);
        if (article.getTime() != null) {
            tv_news_time.setText(article.getTime());
        }
        if (article.getTitle() != null) {
            tv_news_title.setText(article.getTitle());
            setTitle(article.getTitle());
        }
        if (article.getDescription() != null) {
            tv_news_des.setText(article.getDescription());
        }
        if (article.getContent() != null) {
            for (Element element : article.getContent()) {
                Element slide_show = element.getElementsByClass("block_thumb_slide_show").first();
                Element text = element.getElementsByClass("Normal").first();
                Element img = element.getElementsByClass("tplCaption").first();

                if (slide_show != null) {
                    addImageSlide(slide_show.getElementsByTag("img").first());
                } else {
                    if (img != null) {
                        addImage(img.getElementsByTag("img").first());
                    }
                }
                if (text != null) {
                    addContent(text.text());
                }
            }
        }
    }

    private void getSuggestNews() {
        List<RssObject> suggestList = new ArrayList<>();
        RssObject rssObject;
        tv_suggest_news.setText("Tin liên quan");
        tv_suggest_news.setVisibility(View.VISIBLE);
        String link = null, title = null;
        if (document != null) {
//            Elements itemList = document.getElementsByClass("list_news");
//
//            for (Element e : itemList) {
//                Element titleSubject = e.getElementsByClass("title_news").first();
//                Element thumbSubject = e.getElementsByClass("thumb_art").first();
//
//                if (titleSubject != null) {
//                    link = titleSubject.getElementsByTag("a").first().attr("href");
//                    title = titleSubject.getElementsByTag("a").first().attr("title");
//                }
//                try {
//                    if (thumbSubject != null) {
//                        thumb = thumbSubject.getElementsByTag("a").first().getElementsByTag("img").attr("data-original");
//                    } else thumb = null;
//                }catch (Exception ex){
//                    thumb = null;
//                }
//                rssObject = new RssObject(title, link, thumb, null, null);
//                suggestList.add(rssObject);
//            }
            Element element_title = document.getElementsByClass("list_title").first();
            if (element_title != null) {
                Elements list_title = element_title.getElementsByTag("li");
                for (Element element : list_title) {
                    Element titleSubject = element.getElementsByTag("a").first();
                    if (titleSubject != null) {
                        link = titleSubject.attr("href");
                        title = titleSubject.text();
                    }
                    rssObject = new RssObject(title, link, null, null, null);
                    suggestList.add(rssObject);
                }
            }else {
                tv_suggest_news.setText("");
                tv_suggest_news.setVisibility(View.GONE);
            }
        }
        itemAdapter = new RssObjectAdapter(suggestList, new RssItemClick() {
            @Override
            public void onClick(RssObject ob) {
                url = ob.getLink();
                new getHTTPData().execute();
            }
        }, this);
        rv_suggest_news.setAdapter(itemAdapter);
        rv_suggest_news.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }


    private void addContent(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize((float) 20);
        textView.setLayoutParams(params);
        layout_news_normal.addView(textView);
    }

    private void addImage(Element element) {
        String src = element.attr("src");
        String alt = element.attr("alt");

        if (src != null && alt != null) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(params);

            TextView textView = new TextView(this);
            textView.setText(alt);
            textView.setTextSize((float) 10);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);

            ImageView imageView = new ImageView(this);
            try {
                Picasso.with(this).load(src).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.error_image);
            }
            imageView.setLayoutParams(params);

            linearLayout.addView(imageView);
            linearLayout.addView(textView);

            layout_news_normal.addView(linearLayout);
        }
    }

    private void addImageSlide(Element element) {
        String src = element.attr("data-original");
        String alt = element.attr("alt");

        if (src != null && alt != null) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(params);

            TextView textView = new TextView(this);
            textView.setText(alt);
            textView.setTextSize((float) 10);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);

            ImageView imageView = new ImageView(this);
            try {
                Picasso.with(this).load(src).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.error_image);
            }
            imageView.setLayoutParams(params);

            linearLayout.addView(imageView);
            linearLayout.addView(textView);

            layout_news_normal.addView(linearLayout);
        }
    }

    private void addSlideShow(Element element) {
        String src = null;
        String text = null;

        Element imgSubject = element.getElementsByClass("block_thumb_slide_show").first();
        Element textSubject = element.getElementsByClass("Normal").first();

        if (!isNullElemt(imgSubject)) {
            src = imgSubject.getElementsByTag("img").first().attr("data-original");
        }
        if (!isNullElemt(textSubject)) {
            text = imgSubject.text();
        }

        if (src != null && text != null) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(params);

            TextView textView = new TextView(this);
            textView.setText(text);
            textView.setTextSize((float) 10);
            textView.setLayoutParams(params);

            ImageView imageView = new ImageView(this);
            try {
                Picasso.with(this).load(src).into(imageView);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.error_image);
            }
            imageView.setLayoutParams(params);

            linearLayout.addView(imageView);
            linearLayout.addView(textView);

            layout_news_normal.addView(linearLayout);
        }
    }

    private boolean isNullElemt(Element element) {
        return element == null ? true : false;
    }

    private class getHTTPData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout_news_normal.removeAllViews();
            tv_news_time.setText("");
            tv_news_des.setText("");
            tv_news_title.setText("Loading");
            tv_news_title.getParent().requestChildFocus(tv_news_title, tv_news_title);
            tv_suggest_news.setText("");
            rv_suggest_news.setAdapter(null);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            getArticleData(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (article != null) {
                setArticleData();
                getSuggestNews();
            }
        }
    }
}
