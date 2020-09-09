package com.example.helloworld.Rss;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;
import com.example.helloworld.Rss.RssReader.ChungTa_RSS_Reader;
import com.example.helloworld.SQL.DatabaseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class News_View_Fragment extends Fragment {
    RecyclerView rv_news;
    String SERVER_URL = Define.SERVER_URL;
    String PATH_URL;
    List<RssObject> objectList;
    Document document;
    RssObjectAdapter itemAdapter;
    ChungTa_RSS_Reader rssReader;

    TextView tv_loading;
    ProgressBar pb_loading;
    DatabaseHandler databaseHandler;


    public News_View_Fragment(String url) {
        PATH_URL = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_view, container, false);


        rv_news = view.findViewById(R.id.rv_news);
        tv_loading = view.findViewById(R.id.tv_loading_news);
        pb_loading = view.findViewById(R.id.pb_loading_news);
        rssReader = new ChungTa_RSS_Reader();
        databaseHandler = new DatabaseHandler(getContext());
        new HTTPConnect().execute();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void saveRssObject(RssObject object) {
        databaseHandler.addNews(object, Define.TABLE_SAVED_NEWS_NAME, Define.LIMIT_SAVED_NEWS);
    }


    class HTTPConnect extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tv_loading.setText(getString(R.string.loading));
            pb_loading.setIndeterminate(true);
            pb_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rssReader.getArticleList(SERVER_URL + PATH_URL, document, objectList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (document != null) {
                tv_loading.setText("");
                pb_loading.setVisibility(View.INVISIBLE);
                itemAdapter = new RssObjectAdapter(objectList, new IRssItemClick() {
                    @Override
                    public void onClick(RssObject ob) {
                        databaseHandler.addNews(ob, Define.TABLE_RECENTLY_NEWS_NAME, Define.LIMIT_RECENTLY_NEWS);
                        Intent intent = new Intent(getContext(), NewsActivity.class);
                        intent.putExtra(getString(R.string.news_url), ob.getLink());
                        intent.putExtra("rss", ob);
                        startActivity(intent);
                    }

                }, getContext(), databaseHandler);
                rv_news.setAdapter(itemAdapter);
                rv_news.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            } else {
                pb_loading.setVisibility(View.INVISIBLE);
                tv_loading.setText(getString(R.string.disconnect));
            }
        }
    }
}
