package com.example.helloworld.Recently;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.helloworld.Adapter.PagerAdapter;
import com.example.helloworld.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class Recently_Fragment extends Fragment {
    PagerAdapter pagerAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView tv_loading;
    ProgressBar pb_loading;

    public Recently_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        tv_loading = view.findViewById(R.id.tv_loading_tab);
        pb_loading = view.findViewById(R.id.pb_loading_tab);
        tv_loading.setText("");
        pb_loading.setVisibility(View.INVISIBLE);
        tabLayout = view.findViewById(R.id.video_tab_layout);
        viewPager = view.findViewById(R.id.video_tab_viewpager);
        pagerAdapter = new PagerAdapter(getChildFragmentManager());

        pagerAdapter.addTab(new Recently_Videos_Fragment(), getString(R.string.menu_video));
        pagerAdapter.addTab(new Recently_News_Fragment(), getString(R.string.menu_news));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



        return view;
    }

}
