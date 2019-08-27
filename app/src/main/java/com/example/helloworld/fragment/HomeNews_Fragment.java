package com.example.helloworld.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.Entity.Article;
import com.example.helloworld.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HomeNews_Fragment extends Fragment {
    Document document = null;
    ArrayList<Article> articleArrayList;
    String url;

    public HomeNews_Fragment(String url) {
        this.url = url;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);


        return view;
    }


    private void getDataFromWeb(String url) {
        ArrayList<String> listContent = null;
        Article article = null;
        try {
            document = Jsoup.connect(url).get();
            Elements element = document.select("item");
            for (Element e : element) {
                String title = e.select("title").text();
                listContent = cutString(e.select("description").get(0).html());
                String link = listContent.get(0);
                String thumb = listContent.get(1);
                String des = listContent.get(2);

                article = new Article(title, thumb, link, des);
                articleArrayList.add(article);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> cutString(String s) {
        ArrayList<String> arrayList = new ArrayList<>();
        int begin = 0;
        int end = 0;
        String content;
        boolean isNewString = true;
        for (int index = 0; index < s.length(); index++) {
            char c = s.charAt(index);
            if (c == '"') {
                if (isNewString){
                    begin = index;
                    isNewString = false;
                }else {
                    end = index;
                    isNewString = true;
                    content = s.substring(begin+1, end);
                    arrayList.add(content);
                }
            }
        }

        arrayList.add(s.substring(end+13, s.length() - 3));
        return arrayList;
    }

    class getHTTPData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getDataFromWeb(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
