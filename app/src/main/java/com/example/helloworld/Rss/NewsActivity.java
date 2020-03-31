package com.example.helloworld.Rss;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;
import com.example.helloworld.SQL.DatabaseHandler;
import com.squareup.picasso.Picasso;

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
    ActionBar actionbar;
    DatabaseHandler databaseHandler;
    String url;
    String previous_data_original;
    Document document;
    Article article;
    LinearLayout.LayoutParams params;
    ScrollView.LayoutParams scoll_params;

    TextView tv_suggest_news;
    RecyclerView rv_suggest_news;
    SuggestArticleAdapter suggestAdapter;
    RssObject currentRss;

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = findViewById(R.id.toolbar_news);
        tv_news_time = findViewById(R.id.tv_news_time);
        tv_news_title = findViewById(R.id.tv_news_title);
        tv_news_des = findViewById(R.id.tv_news_des);
        layout_news_normal = findViewById(R.id.layout_news_normal);
        tv_suggest_news = findViewById(R.id.tv_suggest_news);
        rv_suggest_news = findViewById(R.id.rv_suggest_news);
//        ibt_up = findViewById(R.id.ibt_up_news);
//        ibt_down = findViewById(R.id.ibt_down_news);
//        ibt_save = findViewById(R.id.ibt_save_news);
        scrollView = findViewById(R.id.scrollview_news);

        params = (LinearLayout.LayoutParams) layout_news_normal.getLayoutParams();
//        scoll_params = (ScrollView.LayoutParams) scrollView.getLayoutParams();
        databaseHandler = new DatabaseHandler(this);


        setUpActionbar();
//        setUpView();

        if (getIntent().getStringExtra(getString(R.string.news_url)) != null) {
            url = getIntent().getStringExtra(getString(R.string.news_url));
        }
        if (getIntent().getSerializableExtra("rss") != null) {
            currentRss = (RssObject) getIntent().getSerializableExtra("rss");
        }


        new getHTTPData().execute();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.nav_save_rss:
                if (!databaseHandler.isContaiNews(url)){
                    databaseHandler.addNews(currentRss, Define.TABLE_SAVED_NEWS_NAME, Define.LIMIT_SAVED_NEWS);
                }
                Toast.makeText(this, Define.STRING_SAVED, Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpActionbar() {
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

    }

    private void setUpView() {
        scoll_params.width = (int) (scoll_params.MATCH_PARENT - getResources().getDimension(R.dimen.ibt_size));
        scoll_params.height = scoll_params.WRAP_CONTENT;

        tv_news_time.setLayoutParams(scoll_params);
        tv_news_title.setLayoutParams(scoll_params);
        tv_news_des.setLayoutParams(scoll_params);
        tv_suggest_news.setLayoutParams(scoll_params);
        layout_news_normal.setLayoutParams(scoll_params);


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
                Element slide_show = element.getElementsByClass(getString(R.string.rss_key_article_element_slide_show)).first();
                Element text = element.getElementsByClass(getString(R.string.rss_key_article_element_text)).first();
                Element img = element.getElementsByClass(getString(R.string.rss_key_article_element_img)).first();

                if (slide_show != null) {
                    addImageSlide(slide_show.getElementsByTag(getString(R.string.rss_key_tag_img)).first());
                } else {
                    if (img != null) {
                        addImage(img.getElementsByTag(getString(R.string.rss_key_tag_img)).first());
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
            Element element_title = document.getElementsByClass(getString(R.string.rss_key_suggest_title_list)).first();
            if (element_title != null) {
                Elements list_title = element_title.getElementsByTag(getString(R.string.rss_key_tag_li));
                for (Element element : list_title) {
                    Element titleSubject = element.getElementsByTag(getString(R.string.rss_key_tag_h4)).first();
                    if (titleSubject != null) {
                        link = titleSubject.getElementsByTag(getString(R.string.rss_key_tag_a)).first().attr(getString(R.string.rss_key_tag_href));
                        title = titleSubject.getElementsByTag(getString(R.string.rss_key_tag_a)).first().attr(getString(R.string.rss_key_tag_title));
                    }
                    rssObject = new RssObject(title, link, null, null, null);
                    suggestList.add(rssObject);
                }
            } else {
                tv_suggest_news.setText("");
                tv_suggest_news.setVisibility(View.GONE);
            }
        }
        suggestAdapter = new SuggestArticleAdapter(suggestList, this, new IRssItemClick() {
            @Override
            public void onClick(RssObject ob) {
                url = ob.getLink();
                currentRss = ob;
                new getHTTPData().execute();
            }

        });
        rv_suggest_news.setAdapter(suggestAdapter);
        rv_suggest_news.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }


    private void addContent(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize((float) 20);
        textView.setLayoutParams(params);
        layout_news_normal.addView(textView);
    }

    private void addImage(Element element) {
        String src = element.attr(getString(R.string.rss_key_src));
        String alt = element.attr(getString(R.string.rss_key_alt));

        if (src != null && alt != null) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(params);

            TextView textView = new TextView(this);
            textView.setText(alt);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize((float) 12);
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
        String src = element.attr(getString(R.string.rss_key_data_original));
        String alt = element.attr("data-component-caption");


        if (src != null && alt != null && !src.equals(previous_data_original)) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(params);

            TextView textView = new TextView(this);
            textView.setText(format_content(alt));
            textView.setTextSize((float) 15);
            textView.setTextColor(Color.BLACK);
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
            previous_data_original = src;
            layout_news_normal.addView(linearLayout);
        }
    }

    private String format_content(String text) {
        text = text.replaceAll("\\&lt;", "");
        text = text.replaceAll("\\/p\\&gt;", "");
        text = text.replaceAll("p\\&gt;", "");
        text = text.replaceAll("p class=\\&quot\\;Normal\\&quot;", "");
        text = text.replaceAll("\\&gt;", "");
        text = text.replaceAll("\\/emspan", "");
        text = text.replaceAll("\\/spanem", "");
        text = text.replaceAll("\\/em", "");
        text = text.replaceAll("\\&quot;", "\"");
        return text;
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
            tv_news_title.setText(getString(R.string.loading));
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
                if (tv_news_time.getText().equals("") && tv_news_des.getText().equals("")){
                    tv_news_title.setText("Not supported article type.");
                }
            }else {
                tv_news_title.setText(getString(R.string.disconnect));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
