package com.example.helloworld.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;
import com.example.helloworld.fragment.Video_Hot_Fragment;
import com.example.helloworld.fragment.Video_List_Fragment;

public class ListVideoActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);

        toolbar = findViewById(R.id.toolbar_list_video);
        setSupportActionBar(toolbar);

        setTitle("List " + getIntent().getStringExtra("title") +" Videos");
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        getSupportFragmentManager().beginTransaction().replace(R.id.layout_list_video, new Video_List_Fragment(Define.CATEGORY_ITEMS_URL)).commit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
