package com.example.helloworld.Rss;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.helloworld.Adapter.PagerAdapter;
import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;
import com.google.android.material.tabs.TabLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class News_Fragment extends Fragment {
    PagerAdapter pagerAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    List<RssObject> objectList;
    Document document;

    TextView tv_loading;
    ProgressBar pb_loading;

    public News_Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);


        tabLayout = view.findViewById(R.id.video_tab_layout);
        viewPager = view.findViewById(R.id.video_tab_viewpager);
        tv_loading = view.findViewById(R.id.tv_loading_tab);
        pb_loading = view.findViewById(R.id.pb_loading_tab);

        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        objectList = new ArrayList<>();

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        new HTTPConnect().execute();
        return view;
    }

    private void getData(String url) {
        RssObject rssObject;
        try {
            document = Jsoup.connect(url).get();
            if (document != null) {
                Element menulist = document.getElementById("main_menu");

                Elements list_title = menulist.getElementsByTag("a");
                for (Element e : list_title) {
                    String link = cutLink(e.attr("href"));
                    String title = e.attr("title");
                    if (!title.isEmpty() && !link.contains(getString(R.string.key_goc_nhin)) && !link.contains("raovat")) {
                        rssObject = new RssObject(title, link, "", "", "");
                        objectList.add(rssObject);
                    }
                }
            }
            objectList.remove(objectList.size() - 1);
            objectList.remove(objectList.size() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    class HTTPConnect extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            tv_loading.setText(getString(R.string.loading));
            pb_loading.setIndeterminate(true);
            pb_loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getData(Define.SERVER_URL);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pb_loading.setVisibility(View.INVISIBLE);
            if (document != null) {
                tv_loading.setText("");
                for (RssObject ro : objectList) {
                    pagerAdapter.addTab(new News_View_Fragment(ro.getLink()), ro.getTitle());
                }
                viewPager.setOffscreenPageLimit(0);
                viewPager.setAdapter(pagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            } else {
                tv_loading.setText(getString(R.string.disconnect));
            }
        }
    }

}
