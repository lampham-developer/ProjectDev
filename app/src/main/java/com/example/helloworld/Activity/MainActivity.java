package com.example.helloworld.Activity;

import android.os.Bundle;
import android.view.MenuItem;

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
import com.example.helloworld.fragment.Video_fragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_main);

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
                        break;
                    case R.id.nav_about:
                        setTitle(getString(R.string.menu_about));
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
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
}
