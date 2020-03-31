package com.example.helloworld.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.helloworld.R;
import com.example.helloworld.Recently.Recently_Fragment;
import com.example.helloworld.Rss.News_Fragment;
import com.example.helloworld.Saved.Saved_Fragment;
import com.example.helloworld.fragment.About_fragment;
import com.example.helloworld.fragment.Video_fragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView tv_say_hi;
    LinearLayout layout_drawer_header;
    boolean run_timer = true;
    Handler timer_handler;
    View header_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_main);

        header_view = navigationView.getHeaderView(0);

        tv_say_hi = header_view.findViewById(R.id.tv_say_hi);
        layout_drawer_header = header_view.findViewById(R.id.layout_drawer_header);

        setFragment(new News_Fragment());
        setTitle(getString(R.string.menu_news));

        setUpActionbar();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        setFragment(new News_Fragment());
                        setTitle(getString(R.string.menu_news));
                        break;
                    case R.id.nav_video:
                        setFragment(new Video_fragment());
                        setTitle(getString(R.string.menu_video));
                        break;
                    case R.id.nav_recently:
                        setFragment(new Recently_Fragment());
                        setTitle(getString(R.string.menu_recently));
                        break;
                    case R.id.nav_saved:
                        setFragment(new Saved_Fragment());
                        setTitle(getString(R.string.menu_saved));
                        break;
                    case R.id.nav_version:
                        setFragment(new About_fragment());
                        setTitle(getString(R.string.menu_version));
                        break;
                    case R.id.nav_about:
                        setFragment(new About_fragment());
                        setTitle(getString(R.string.menu_about));
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        timer_handler = new Handler();
        timer();
    }

    private void setUpActionbar() {

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (run_timer) {
                    try {
                        Thread.sleep(1000);
                        timer_handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar = Calendar.getInstance();
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);

                                if (hour < 6 || hour >= 22) {
                                    tv_say_hi.setText(getString(R.string.header_night));
                                    layout_drawer_header.setBackgroundResource(R.drawable.header_night_background);
                                }
                                if (hour >= 6 && hour < 12) {
                                    tv_say_hi.setText(getString(R.string.header_morning));
                                    layout_drawer_header.setBackgroundResource(R.drawable.header_morning_background);
                                }
                                if (hour >= 12 && hour < 18) {
                                    tv_say_hi.setText(getString(R.string.header_aftnoon));
                                    layout_drawer_header.setBackgroundResource(R.drawable.header_afternoon_background);
                                }
                                if (hour >= 18 && hour < 22) {
                                    tv_say_hi.setText(getString(R.string.header_evening));
                                    layout_drawer_header.setBackgroundResource(R.drawable.header_evening_background);
                                }

                            }
                        });

                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }
}
