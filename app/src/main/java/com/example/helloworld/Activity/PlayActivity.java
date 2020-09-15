package com.example.helloworld.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Adapter.SuggestVideoAdapter;
import com.example.helloworld.Entity.Define;
import com.example.helloworld.Entity.Video;
import com.example.helloworld.Interface.IVideoClick;
import com.example.helloworld.R;
import com.example.helloworld.SQL.DatabaseHandler;
import com.example.helloworld.Web_API.CallAPI;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PlayActivity extends AppCompatActivity {

    Toolbar toolbar;
    ActionBar actionbar;
    Video current_video;
    PlayerView playerView;
    ExoPlayer exoPlayer;
    RecyclerView recyclerView;
    TextView tv_suggest_video;

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
    long currentPosition;

    int firstX, firstY, lastX, lastY;
    boolean isChangePotision = true, isChangeVolume = true;

    RelativeLayout layout_forward_state;
    ImageView img_forward_state;
    TextView tv_cur_position, tv_duration;

    LinearLayout layout_volume;
    TextView tv_cur_volume;
    ImageView img_volume;
    int maxVolume;
    double volumeStep;
    int currentVolume;

    DatabaseHandler databaseHandler;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        callAPI = new CallAPI();

        bt_full_screen = findViewById(R.id.exo_fullscreen_button);
        pb_suggest_video = findViewById(R.id.pb_suggest_video);
        recyclerView = findViewById(R.id.rv_suggest);
        tv_suggest_video = findViewById(R.id.tv_suggest_video);
        layout_forward_state = findViewById(R.id.layout_forward_state);
        img_forward_state = findViewById(R.id.img_forward_state);
        tv_cur_position = findViewById(R.id.tv_cur_position);
        tv_duration = findViewById(R.id.tv_duration);
        layout_volume = findViewById(R.id.layout_volume);
        tv_cur_volume = findViewById(R.id.tv_cur_volume);
        img_volume = findViewById(R.id.img_volume);
        playerView = findViewById(R.id.pv_playing_video);
        toolbar = findViewById(R.id.toolbar_playing);

        databaseHandler = new DatabaseHandler(this);

        setUpActionbar();
        setUpVolumeStream();
        setUpPlayerView();
        getStartVideo();

        startPlayVideo(current_video);
        currentPosition = exoPlayer.getCurrentPosition();

    }

    private void setUpActionbar() {
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
    }

    private void setUpVolumeStream() {
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeStep = 100 / maxVolume;
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        currentVolume = (int) (currentVolume * volumeStep);
    }

    private void setUpPlayerView() {
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

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        tv_duration.setText(formatDuration(exoPlayer.getDuration()));
                        firstX = (int) motionEvent.getX();
                        firstY = (int) motionEvent.getY();
                        lastX = (int) motionEvent.getX();
                        lastY = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs((int) motionEvent.getY() - firstY) > Define.SWIPE_THRESHOLD_Y)
                            isChangePotision = false;
                        if (Math.abs((int) motionEvent.getX() - firstX) > Define.SWIPE_THRESHOLD_X)
                            isChangeVolume = false;
                        if (Math.abs((int) motionEvent.getX() - firstX) > Define.SWIPE_THRESHOLD_X && isChangePotision) {
                            layout_forward_state.setVisibility(View.VISIBLE);

                            if ((int) motionEvent.getX() - lastX < 0)
                                img_forward_state.setImageResource(R.drawable.ic_fast_rewind);
                            if ((int) motionEvent.getX() - lastX > 0)
                                img_forward_state.setImageResource(R.drawable.ic_fast_forward);

                            currentPosition = exoPlayer.getCurrentPosition() + ((int) motionEvent.getX() - lastX) * 200;
                            if (currentPosition >= 0 && currentPosition <= exoPlayer.getDuration()) {
                                exoPlayer.seekTo(currentPosition);
                                tv_cur_position.setText(formatDuration(currentPosition));
                            }
                            lastX = (int) motionEvent.getX();
                        }

                        if (Math.abs((int) motionEvent.getY() - firstY) > Define.SWIPE_THRESHOLD_Y && isChangeVolume) {
                            layout_volume.setVisibility(View.VISIBLE);
                            if ((int) motionEvent.getY() - lastY > 0 && 0 < currentVolume) {
                                currentVolume--;
                            }
                            if ((int) motionEvent.getY() - lastY < 0 && currentVolume < 100) {
                                currentVolume++;
                            }
                            lastY = (int) motionEvent.getY();
                            setChangeVolume();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        layout_forward_state.setVisibility(View.INVISIBLE);
                        layout_volume.setVisibility(View.INVISIBLE);
                        isChangePotision = true;
                        isChangeVolume = true;
                        break;
                }
                playerView.showController();
                return true;
            }
        });
    }

    private void getStartVideo() {
        current_video = (Video) getIntent().getSerializableExtra(getString(R.string.intent_video));
        url = getIntent().getStringExtra(getString(R.string.intent_url));
        if (getIntent().getStringExtra(getString(R.string.intent_category)) != null
                && getIntent().getStringExtra(getString(R.string.intent_category)).equals(getString(R.string.itent_category_hot)))
            setFullScreen();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                stopPlayVideo();
                finish();
                return true;
            case R.id.nav_save_rss:
                if (!databaseHandler.isContaiVideo(current_video)){
                    databaseHandler.addVideo(current_video, Define.TABLE_SAVED_VIDEOS_NAME, Define.LIMIT_SAVED_VIDEOS);
                }
                Toast.makeText(this, Define.STRING_SAVED, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startPlayVideo(Video video) {
        stopPlayVideo();
        recyclerView.setAdapter(null);
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

        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_ENDED:
                        current_video = videoList.get(0);
                        videoAdapter = new SuggestVideoAdapter(videoList, getBaseContext(), new IVideoClick() {
                            @Override
                            public void onClick(Video video) {
                                databaseHandler.addVideo(video, Define.TABLE_RECENTLY_VIDEOS_NAME, Define.LIMIT_RECENTLY_VIDEOS);
                                current_video = video;
                                startPlayVideo(video);
                            }
                        }, current_video, databaseHandler);
                        recyclerView.setAdapter(videoAdapter);
                        databaseHandler.addVideo(current_video, Define.TABLE_RECENTLY_VIDEOS_NAME, Define.LIMIT_RECENTLY_VIDEOS);
                        startPlayVideo(current_video);
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                stopPlayVideo();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

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
//        recyclerView.setAdapter(videoAdapter);
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
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setFullScreen();
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setWindowScreen();
        }
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
                videoAdapter = new SuggestVideoAdapter(videoList, getBaseContext(), new IVideoClick() {
                    @Override
                    public void onClick(Video video) {
                        databaseHandler.addVideo(video, Define.TABLE_RECENTLY_VIDEOS_NAME, Define.LIMIT_RECENTLY_VIDEOS);
                        current_video = video;
                        startPlayVideo(video);
                    }
                }, current_video, databaseHandler);
                recyclerView.setAdapter(videoAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
            } else {
                pb_suggest_video.setVisibility(View.INVISIBLE);
                tv_suggest_video.setText(getString(R.string.disconnect));
            }
        }
    }

    private String formatDuration(long duration) {
        return new SimpleDateFormat("mm:ss").format(duration);
    }

    private void setChangeVolume() {
        int volume = (int) (currentVolume / volumeStep);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        tv_cur_volume.setText(String.valueOf(currentVolume));
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
