package com.example.helloworld.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.helloworld.Adapter.PagerAdapter;
import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;
import com.google.android.material.tabs.TabLayout;

public class Video_fragment extends Fragment {

    public Video_fragment() {
    }

    Video_Hot_Fragment video_hot_fragment;
    Video_Categories_Fragment video_categories_fragment;
    PagerAdapter pagerAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video,container, false);
        video_hot_fragment = new Video_Hot_Fragment(Define.HOT_VIDEO_URL);
        video_categories_fragment = new Video_Categories_Fragment();

        tabLayout = view.findViewById(R.id.video_tab_layout);
        viewPager = view.findViewById(R.id.video_tab_viewpager);
        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        pagerAdapter.addTab(video_hot_fragment, getString(R.string.video_tab_1));
        pagerAdapter.addTab(video_categories_fragment, getString(R.string.video_tab_2));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


}
