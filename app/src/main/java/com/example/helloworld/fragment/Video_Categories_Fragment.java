package com.example.helloworld.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.R;

public class Video_Categories_Fragment extends Fragment {

    public Video_Categories_Fragment() {
    }

    public static Video_Categories_Fragment newInstance() {
        return new Video_Categories_Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categories_video, container , false);
        return view;
    }
}
