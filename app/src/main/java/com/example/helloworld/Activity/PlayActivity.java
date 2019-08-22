package com.example.helloworld.Activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class PlayActivity extends AppCompatActivity {

    Toolbar toolbar;
    ActionBar actionbar;
    Video video;
    PlayerView playerView;
    ExoPlayer exoPlayer;
    RecyclerView recyclerView;
    TextView tv_suggest_video;
    TextView tv_position;
    String json;
    List<Video> videoList;
    String category = "null";
    String url;
    CallAPI callAPI;
    ProgressBar pb_suggest_video;
    ImageButton bt_full_screen;
    int screen_sate;
    SuggestVideoAdapter videoAdapter;

    AudioManager audioManager;
    GestureDetector gestureDetector;
    long currentPosition;
    Handler handle;

    int firstX, firstY;
    boolean isChangePotition = true, isChangeVolume = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        callAPI = new CallAPI();

        bt_full_screen = findViewById(R.id.exo_fullscreen_button);
        pb_suggest_video = findViewById(R.id.pb_suggest_video);
        recyclerView = findViewById(R.id.rv_suggest);
        tv_suggest_video = findViewById(R.id.tv_suggest_video);
        tv_position = findViewById(R.id.tv_position);
        playerView = findViewById(R.id.pv_playing_video);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        toolbar = findViewById(R.id.toolbar_playing);
        handle = new Handler();

        gestureDetector = new GestureDetector(getBaseContext(), new MyGesture());
        setSupportActionBar(toolbar);

        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        screen_sate = Define.WINDOW_SCREEN;
        bt_full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (screen_sate == Define.FULL_SCREEN) {
                    setWindowScreen();
                } else {
                    setFullScreen();
                }
            }
        });


        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                gestureDetector.onTouchEvent(motionEvent);

                switch (motionEvent.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        firstX =(int) motionEvent.getX();
                        firstY =(int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs((int) motionEvent.getY() - firstY) > Define.SWIPE_THRESHOLD_Y) isChangePotition = false;
                        if (Math.abs((int) motionEvent.getX() - firstX) > Define.SWIPE_THRESHOLD_X) isChangeVolume = false;
                        if(Math.abs((int) motionEvent.getY() - firstY) < Define.SWIPE_THRESHOLD_Y && isChangePotition){
                            exoPlayer.seekTo(exoPlayer.getCurrentPosition() + ((int)motionEvent.getX() - firstX)*100);
                            tv_position.setText(String.valueOf(exoPlayer.getCurrentPosition()));
                            firstX =(int) motionEvent.getX();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isChangePotition = true;
                        isChangeVolume = true;
                        break;
                }
                playerView.showController();
                return true;
            }
        });

        video = (Video) getIntent().getSerializableExtra(getString(R.string.intent_video));
        url = getIntent().getStringExtra(getString(R.string.intent_url));

        startPlayVideo(video);

        if(getIntent().getStringExtra(getString(R.string.intent_category)).equals("hot"))setFullScreen();


        currentPosition = exoPlayer.getCurrentPosition();
        updateTimebarAction.run();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                stopPlayVideo();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startPlayVideo(Video video) {
        stopPlayVideo();
        recyclerView.setAdapter(null);
        tv_position.setVisibility(View.INVISIBLE);
        new VideoHTTP(url).execute();
        setTitle(video.getTitle());
        String video_url = video.getMp4_url();
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(getBaseContext());

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        String userAgent = Util.getUserAgent(getBaseContext(), getString(R.string.app_name));
        Uri uri = Uri.parse(video_url);

        MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getBaseContext(), userAgent)).setExtractorsFactory(new DefaultExtractorsFactory()).createMediaSource(uri);


        playerView.setFitsSystemWindows(true);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        playerView.setPlayer(exoPlayer);
    }

    private void setFullScreen() {
        bt_full_screen.setImageResource(R.drawable.ic_fullscreen_skrink);
        getSupportActionBar().hide();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.height = params.MATCH_PARENT;
        params.width = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
        screen_sate = Define.FULL_SCREEN;
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void setWindowScreen() {
        bt_full_screen.setImageResource(R.drawable.ic_fullscreen_expand);
        recyclerView.setAdapter(videoAdapter);
        getSupportActionBar().show();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        params.height = params.WRAP_CONTENT;
        params.width = params.MATCH_PARENT;
        playerView.setLayoutParams(params);
        screen_sate = Define.WINDOW_SCREEN;
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    private void stopPlayVideo() {
        try {
            exoPlayer.stop();
            exoPlayer.release();
        } catch (Exception e) {

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        recyclerView.setAdapter(videoAdapter);
        super.onConfigurationChanged(newConfig);
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
            if (json != null) {
                tv_suggest_video.setText("");
                pb_suggest_video.setVisibility(View.INVISIBLE);
                videoList = callAPI.getListVideo(json, category, true);
                videoAdapter = new SuggestVideoAdapter(videoList, getBaseContext(), new VideoClick() {
                    @Override
                    public void onClick(Video video) {
                        startPlayVideo(video);
                    }
                }, video);
                recyclerView.setAdapter(videoAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
            } else {
                pb_suggest_video.setVisibility(View.INVISIBLE);
                tv_suggest_video.setText(getString(R.string.disconnect));
            }
        }
    }

    private void updateTimebar(long position) {
        tv_position.setText(String.valueOf(position));
        handle.postDelayed(updateTimebarAction, 100);
    }

    private final Runnable updateTimebarAction = new Runnable() {
        @Override
        public void run() {
            updateTimebar(exoPlayer.getCurrentPosition());
        }
    };

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    class MyGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            playerView.showController();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if(e.getX() < getScreenWidth()/3) exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
            if(e.getX() > (getScreenWidth()*2/3)) exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);

            playerView.showController();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            tv_position.setVisibility(View.VISIBLE);
            //trai qua phai
            if (e2.getX() - e1.getX() > Define.SWIPE_THRESHOLD_X && Math.abs(velocityX) > Define.SWIPE_VELO_THRESHOLD) {
                currentPosition = exoPlayer.getCurrentPosition();
                long distance = (long) (e2.getX() - e1.getX());
                currentPosition = currentPosition + distance * 100;
                tv_position.setText(String.valueOf(currentPosition / 1000));
                exoPlayer.seekTo(currentPosition);
                playerView.showController();
            }
            //phai qua trai
            if (e1.getX() - e2.getX() > Define.SWIPE_THRESHOLD_X && Math.abs(velocityX) > Define.SWIPE_VELO_THRESHOLD) {
                currentPosition = exoPlayer.getCurrentPosition();
                long distance = (long) (e1.getX() - e2.getX());
                currentPosition = currentPosition - distance * 100;
                tv_position.setText(String.valueOf(currentPosition / 1000));
                exoPlayer.seekTo(currentPosition);
                playerView.showController();

            }
            //tren xuong duoi
            if (e2.getY() - e1.getY() > Define.SWIPE_THRESHOLD_Y && Math.abs(velocityY) > Define.SWIPE_VELO_THRESHOLD) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND);
            }
            //duoi len tren
            if (e1.getY() - e2.getY() > Define.SWIPE_THRESHOLD_Y && Math.abs(velocityY) > Define.SWIPE_VELO_THRESHOLD) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND);
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }


}
