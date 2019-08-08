package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.helloworld.fragment.About_fragment;
import com.example.helloworld.fragment.Video_Hot_Fragment;
import com.example.helloworld.fragment.Video_fragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    Video_fragment video_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        video_fragment = new Video_fragment();

        setFragment(video_fragment);
        setTitle(getString(R.string.menu_home));

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_home :
                        setFragment(video_fragment);
                        setTitle(getString(R.string.menu_home));
                        break;
                    case R.id.nav_video :
                        setFragment(video_fragment);
                        setTitle(getString(R.string.menu_video));
                        break;
                     case R.id.nav_version :
                         getSupportFragmentManager().beginTransaction().replace(R.id.content_frame , new Video_Hot_Fragment()).commit();
                        break;
                     case R.id.nav_about :
                         getSupportFragmentManager().beginTransaction().replace(R.id.content_frame , new About_fragment()).commit();
                         setTitle(getString(R.string.menu_about));
                        break;
                }
                drawerLayout.closeDrawers();

                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
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
            Log.d("Main ", "getFragment: " + e.getMessage());
        }
    }
}
