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

import com.example.helloworld.Activity.PlayActivity;
import com.example.helloworld.Adapter.VideoAdapter;
import com.example.helloworld.Entity.Define;
import com.example.helloworld.Entity.Video;
import com.example.helloworld.Interface.IVideoClick;
import com.example.helloworld.R;
import com.example.helloworld.SQL.DatabaseHandler;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Saved_Video_Fragment extends Fragment {
    DatabaseHandler databaseHandler;
    RecyclerView recyclerView;
    List<Video> videoList;
    ProgressBar pb_video_list;
    TextView tv_loading_video_list;

    public Saved_Video_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        pb_video_list = view.findViewById(R.id.pb_video_list);
        recyclerView = view.findViewById(R.id.rv_video_list);
        tv_loading_video_list = view.findViewById(R.id.tv_loading_video_list);

        tv_loading_video_list.setText("");
        pb_video_list.setVisibility(View.INVISIBLE);

        databaseHandler = new DatabaseHandler(getContext());
        videoList = databaseHandler.getAllVideos(Define.TABLE_SAVED_VIDEOS_NAME);
        VideoAdapter videoAdapter = new VideoAdapter(videoList, getContext(), new IVideoClick() {
            @Override
            public void onClick(Video video) {
                Intent intent = new Intent(getContext(), PlayActivity.class);
                intent.putExtra(getString(R.string.intent_video), video);
                intent.putExtra(getString(R.string.intent_url), Define.CATEGORY_ITEMS_URL);
                startActivity(intent);
            }
        }, databaseHandler);
        recyclerView.setAdapter(videoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        return view;
    }

}
