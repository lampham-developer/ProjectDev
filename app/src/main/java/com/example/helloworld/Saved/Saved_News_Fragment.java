package com.example.helloworld.Saved;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;
import com.example.helloworld.News.Entities.IRssItemClick;
import com.example.helloworld.News.Activity.NewsActivity;
import com.example.helloworld.News.Entities.RssObject;
import com.example.helloworld.News.Adapter.RssObjectAdapter;
import com.example.helloworld.SQL.DatabaseHandler;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Saved_News_Fragment extends Fragment {
    RecyclerView rv_news;
    List<RssObject> objectList;
    RssObjectAdapter itemAdapter;

    TextView tv_loading;
    ProgressBar pb_loading;
    DatabaseHandler databaseHandler;

    public Saved_News_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_view, container, false);

        rv_news = view.findViewById(R.id.rv_news);
        tv_loading = view.findViewById(R.id.tv_loading_news);
        pb_loading = view.findViewById(R.id.pb_loading_news);

        tv_loading.setText("");
        pb_loading.setVisibility(View.INVISIBLE);

        databaseHandler = new DatabaseHandler(getContext());

        objectList = databaseHandler.getAllRssObject(Define.TABLE_SAVED_NEWS_NAME);
        itemAdapter = new RssObjectAdapter(objectList, new IRssItemClick() {
            @Override
            public void onClick(RssObject ob) {
                Intent intent = new Intent(getContext(), NewsActivity.class);
                intent.putExtra(getString(R.string.news_url), ob.getLink());
                startActivity(intent);
            }

        }, getContext(), databaseHandler);
        rv_news.setAdapter(itemAdapter);
        rv_news.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        return view;
    }

}
