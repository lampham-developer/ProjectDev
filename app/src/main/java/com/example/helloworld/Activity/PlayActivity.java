package com.example.helloworld.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.Adapter.SuggestVideoAdapter;
import com.example.helloworld.Entity.Define;
import com.example.helloworld.Entity.Video;
import com.example.helloworld.Interface.VideoClick;
import com.example.helloworld.R;
import com.example.helloworld.Web_API.CallAPI;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class PlayActivity extends AppCompatActivity {

    Toolbar toolbar;
    Video video;
    PlayerView playerView;
    ExoPlayer exoPlayer;
    RecyclerView recyclerView;
    TextView tv_suggest_video;
    String json;
    List<Video> videoList;
    String category = "null";
    String url ;
    CallAPI callAPI;
    ProgressBar pb_suggest_video;
    ImageButton bt_full_screen;
    int screen_sate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        callAPI = new CallAPI();

        bt_full_screen = findViewById(R.id.exo_fullscreen_button);
        pb_suggest_video = findViewById(R.id.pb_suggest_video);
        recyclerView = findViewById(R.id.rv_suggest);
        tv_suggest_video = findViewById(R.id.tv_suggest_video);
        playerView = findViewById(R.id.pv_playing_video);
        toolbar = findViewById(R.id.toolbar_playing);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        bt_full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(screen_sate == Define.FULL_SCREEN){
                    setWindowScreen();
                }else{
                    setFullScreen();
                }
            }
        });
        video = (Video) getIntent().getSerializableExtra("video");
        url = getIntent().getStringExtra("url");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startPlayVideo(video);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                stopPlayVideo();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startPlayVideo(Video video){
        stopPlayVideo();
        recyclerView.setAdapter(null);
        new VideoHTTP(url).execute();
        setTitle(video.getTitle());
        String url = video.getMp4_url();
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(getBaseContext());

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        String userAgent = Util.getUserAgent(getBaseContext(), getString(R.string.app_name));
        Uri uri = Uri.parse(url);

        MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getBaseContext(), userAgent)).setExtractorsFactory(new DefaultExtractorsFactory()).createMediaSource(uri);

        playerView.setFitsSystemWindows(true);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        playerView.setPlayer(exoPlayer);

    }

    private void setFullScreen(){
        bt_full_screen.setImageResource(R.drawable.ic_fullscreen_skrink);
        RelativeLayout.LayoutParams params =(RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.height = params.MATCH_PARENT;
        params.width = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
        screen_sate = Define.FULL_SCREEN;
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    private void setWindowScreen(){
        bt_full_screen.setImageResource(R.drawable.ic_fullscreen_expand);
        RelativeLayout.LayoutParams params =(RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.height = params.WRAP_CONTENT;
        params.width = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
        screen_sate = Define.WINDOW_SCREEN;
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        }
    }

    private void stopPlayVideo(){
        try {
            exoPlayer.stop();
            exoPlayer.release();
        }catch (Exception e){

        }
    }


    class VideoHTTP extends AsyncTask<String, Void, String> {
        String url;
        public VideoHTTP(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            tv_suggest_video.setText(getString(R.string.loading));
            pb_suggest_video.setIndeterminate(true);
            pb_suggest_video.setVisibility(View.VISIBLE);
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
            if(json != null){
                tv_suggest_video.setText("");
                pb_suggest_video.setVisibility(View.INVISIBLE);
                videoList = callAPI.getListVideo(json, category, true);
                SuggestVideoAdapter videoAdapter = new SuggestVideoAdapter(videoList, getBaseContext(), new VideoClick() {
                    @Override
                    public void onClick(Video video) {
                        startPlayVideo(video);
                    }
                }, video);
                recyclerView.setAdapter(videoAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
            }else{
                pb_suggest_video.setVisibility(View.INVISIBLE);
                tv_suggest_video.setText(getString(R.string.disconnect));
            }
        }

    }
}
