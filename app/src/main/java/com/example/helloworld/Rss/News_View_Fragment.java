package com.example.helloworld.Rss;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;

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

    TextView tv_loading;
    ProgressBar pb_loading;

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
        new HTTPConnect().execute();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void getData(String url) {
        RssObject rssObject;
        objectList = new ArrayList<>();
        String link = null,title=null,des=null, thumb=null;
        try {
            document = Jsoup.connect(url).get();
            if (document != null) {
                Elements itemList = document.getElementsByClass("sidebar_1").first().getElementsByClass("list_news");

                for (Element e : itemList) {
                    Element titleSubject = e.getElementsByClass("title_news").first();
                    Element desSubject = e.getElementsByClass("description").first();
                    Element thumbSubject = e.getElementsByClass("thumb_art").first();

                        if (titleSubject!= null) {
                            link = titleSubject.getElementsByTag("a").first().attr("href");
                            title = titleSubject.getElementsByTag("a").first().attr("title");
                        }
                        if (desSubject!= null){
                            des = e.getElementsByClass("description").text();
                        }
                        if (thumbSubject!= null){
                            thumb = thumbSubject.getElementsByTag("a").first().getElementsByTag("img").attr("data-original");
                        }


                        rssObject = new RssObject(title, link, thumb, des, "");
                        objectList.add(rssObject);

                }
            }
        } catch (IOException e) {
        }
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
            getData(SERVER_URL + PATH_URL);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (document != null) {
                tv_loading.setText("");
                pb_loading.setVisibility(View.INVISIBLE);
                itemAdapter = new RssObjectAdapter(objectList, new RssItemClick() {
                    @Override
                    public void onClick(RssObject ob) {
                        Toast.makeText(getContext(), ob.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }, getContext());

                rv_news.setAdapter(itemAdapter);
                rv_news.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            } else {
                pb_loading.setVisibility(View.INVISIBLE);
                tv_loading.setText(getString(R.string.disconnect));
            }
        }
    }
}
