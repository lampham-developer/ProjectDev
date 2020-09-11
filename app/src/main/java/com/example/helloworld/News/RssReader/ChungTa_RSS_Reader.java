package com.example.helloworld.News.RssReader;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.News.Activity.NewsActivity;
import com.example.helloworld.News.Adapter.SuggestArticleAdapter;
import com.example.helloworld.News.Entities.Article;
import com.example.helloworld.News.Entities.IRssItemClick;
import com.example.helloworld.News.Entities.RssObject;
import com.example.helloworld.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChungTa_RSS_Reader {
    public List<RssObject> getMenuList(Document document, List<RssObject> objectList) {
        RssObject rssObject;

                Element menulist = document.getElementsByClass("divblock menu_div").get(0);

                Elements list_title = menulist.getElementsByTag("a");
                for (Element e : list_title) {
                    String link = cutLink(e.attr("href"));
                    if(link == "" || link.isEmpty()) continue;
                    String title = e.attr("title");
                    rssObject = new RssObject();
                    rssObject.setLink(link);
                    rssObject.setTitle(title);

                    objectList.add(rssObject);
                }
        objectList.remove(objectList.size()-1);
        return objectList;
    }

    public List<RssObject> getArticleList(Document document, List<RssObject> objectList) {
        RssObject rssObject;
        objectList = new ArrayList<>();
        String link = null, title = null, des = null, thumb = null;

                Elements itemList = document.getElementsByClass("ul-folder").first().getElementsByTag("li");

                for (Element e : itemList) {
                    Element titleSubject = e.getElementsByClass("title").first();
                    Element desSubject = e.getElementsByClass("lead").first();
                    Element thumbSubject = e.getElementsByClass("thumb-art").first();

                    if (titleSubject != null) {
                        link = titleSubject.getElementsByTag("a").first().attr("href");
                        title = titleSubject.getElementsByTag("a").first().attr("title");
                    }
                    if (desSubject != null) {
                        des = e.getElementsByClass("lead").text();
                    }
                    if (thumbSubject != null) {
                        thumb = thumbSubject.getElementsByTag("a").first().getElementsByTag("img").attr("src");
                    } else thumb = null;


                    rssObject = new RssObject(title, link, thumb, des, "");
                    objectList.add(rssObject);

                }
        return objectList;
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



    private void getArticleData(String news_url, Document document) {
        try {
            document = Jsoup.connect(news_url).get();
            article = null;
            String time = null, title = null, des = null;
            List<Element> contentList = null;
            if (document != null) {
                Element itemList = document.getElementsByClass("fck_detail width_common").first();
                Element timeSubject = document.getElementsByClass("clock_current").first();
                if (document != null) {
                    Element titleSubject = document.getElementsByClass("title_news").first();
                    Element desSubject = document.getElementsByClass("short_intro").first();
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
            e.printStackTrace();
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
                new NewsActivity.getHTTPData().execute();
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
}
