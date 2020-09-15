package com.example.helloworld.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Activity.PlayActivity;
import com.example.helloworld.Adapter.VideoAdapter;
import com.example.helloworld.Entity.Define;
import com.example.helloworld.Entity.Video;
import com.example.helloworld.Interface.IVideoClick;
import com.example.helloworld.R;
import com.example.helloworld.SQL.DatabaseHandler;
import com.example.helloworld.Web_API.CallAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Video_List_Fragment extends Fragment {

    RecyclerView recyclerView;
    List<Video> videoList;
    String json;
    TextView tv_loading_video_list;
    String videoUrl;
    ProgressBar pb_video_list;
    DatabaseHandler databaseHandler;

    public Video_List_Fragment(String videoUrl) {
        // Required empty public constructor
        this.videoUrl = videoUrl;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        pb_video_list = view.findViewById(R.id.pb_video_list);
        recyclerView = view.findViewById(R.id.rv_video_list);
        tv_loading_video_list = view.findViewById(R.id.tv_loading_video_list);

        databaseHandler = new DatabaseHandler(getContext());
        videoList = new ArrayList<>();
        new Video_List_Fragment.VideoHTTP(videoUrl).execute();

        return view;
    }

    private List<Video> getListVideo(String json) {
        List<Video> currentList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            int count = 0;
            while (jsonArray.getJSONObject(count) != null) {
                JSONObject jsonObject = jsonArray.getJSONObject(count);

                String title = jsonObject.getString(getString(R.string.video_title));
                String artis = jsonObject.getString(getString(R.string.video_artist_name));
                String date = jsonObject.getString(getString(R.string.video_date_published));
                String avt_url = jsonObject.getString(getString(R.string.video_avatar));
                String mp4_url = jsonObject.getString(getString(R.string.video_file_mp4));

                Video video = new Video(title, date, artis, avt_url, mp4_url);
                currentList.add(video);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currentList;

    }


    class VideoHTTP extends AsyncTask<String, Void, String> {
        String url;

        public VideoHTTP(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            tv_loading_video_list.setText(getString(R.string.loading));
            pb_video_list.setIndeterminate(true);
            pb_video_list.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            json = new CallAPI().getJsonFromWeb(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (json != null) {
                tv_loading_video_list.setText("");
                pb_video_list.setVisibility(View.INVISIBLE);
                videoList = getListVideo(json);
                VideoAdapter videoAdapter = new VideoAdapter(videoList, getContext(), new IVideoClick() {
                    @Override
                    public void onClick(Video video) {
                        databaseHandler.addVideo(video, Define.TABLE_RECENTLY_VIDEOS_NAME, Define.LIMIT_RECENTLY_VIDEOS);
                        Intent intent = new Intent(getContext(), PlayActivity.class);
                        intent.putExtra(getString(R.string.intent_video), video);
                        intent.putExtra(getString(R.string.intent_url), Define.CATEGORY_ITEMS_URL);
                        startActivity(intent);
                    }
                }, databaseHandler);
                recyclerView.setAdapter(videoAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            } else {
                pb_video_list.setVisibility(View.INVISIBLE);
                tv_loading_video_list.setText(getString(R.string.disconnect));
            }
        }

    }

}
