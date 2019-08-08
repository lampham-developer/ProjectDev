package com.example.helloworld.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.R;

public class Video_Hot_Fragment extends Fragment {

    public Video_Hot_Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hot_video, container , false);
        TextView test = view.findViewById(R.id.test);
        test.setText("OK TEST");
        return view;
    }

    public static Video_Hot_Fragment newInstance() {
        return new Video_Hot_Fragment();
    }
}
