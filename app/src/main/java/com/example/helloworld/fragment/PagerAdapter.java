package com.example.helloworld.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    Video_Hot_Fragment video_hot_fragment;
    Video_Categories_Fragment video_categories_fragment;
    String title_tab_1, title_tab_2;

    public PagerAdapter(FragmentManager fm, Video_Hot_Fragment video_hot_fragment, Video_Categories_Fragment video_categories_fragment,  String title_tab_1,  String title_tab_2) {
        super(fm);
        this.video_hot_fragment = video_hot_fragment;
        this.video_categories_fragment = video_categories_fragment;
        this.title_tab_1 = title_tab_1;
        this.title_tab_2 = title_tab_2;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return video_hot_fragment.newInstance();
        }if (position == 1) {
            return video_categories_fragment.newInstance();
        }else return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return title_tab_1;
        }if (position == 1) {
            return title_tab_2;
        }else return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
