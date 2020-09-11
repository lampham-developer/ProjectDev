package com.example.helloworld.News.Activity;

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
import com.example.helloworld.News.Adapter.SuggestArticleAdapter;
import com.example.helloworld.News.Entities.Article;
import com.example.helloworld.News.Entities.IRssItemClick;
import com.example.helloworld.News.Entities.RssObject;
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
