package com.example.helloworld.News.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.helloworld.R;

public class SavedNewsActivity extends AppCompatActivity {
    TextView tv_saved_news_time, tv_saved_news_title, tv_saved_news_des;
    LinearLayout layout_saved_news_normal;
    Toolbar toolbar;
    ActionBar actionbar;
    LinearLayout.LayoutParams params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        tv_saved_news_time = findViewById(R.id.tv_saved_news_time);
        tv_saved_news_title = findViewById(R.id.tv_saved_news_title);
        tv_saved_news_des = findViewById(R.id.tv_saved_news_des);
        layout_saved_news_normal = findViewById(R.id.layout_saved_news_normal);
        toolbar = findViewById(R.id.toolbar_saved_news);

        params = (LinearLayout.LayoutParams) layout_saved_news_normal.getLayoutParams();

        setUpActionbar();
    }

    private void setUpActionbar() {
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

    }
}
