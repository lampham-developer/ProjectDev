package com.example.helloworld.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Activity.ListVideoActivity;
import com.example.helloworld.Adapter.CategoryAdapter;
import com.example.helloworld.Entity.Define;
import com.example.helloworld.Entity.Video;
import com.example.helloworld.Entity.VideoCategory;
import com.example.helloworld.Interface.CategoryClick;
import com.example.helloworld.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Video_Categories_Fragment extends Fragment {

    RecyclerView recyclerView;
    TextView tv_loading_category;
    String json;
    List<VideoCategory> videoCategoryList;

    public Video_Categories_Fragment() {
    }

    public static Video_Categories_Fragment newInstance() {
        return new Video_Categories_Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categories_video, container , false);
        recyclerView = view.findViewById(R.id.rv_category);
        tv_loading_category = view.findViewById(R.id.tv_loading_category);
        videoCategoryList = new ArrayList<>();
        new CategoryHTTP(Define.CATEGORY_URL).execute();
        return view;
    }

    private String getJsonFromWeb(String url){
        String result = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<VideoCategory> getListCategory(String json){
        List<VideoCategory> currentList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            int count = 0;
            while (jsonArray.getJSONObject(count) != null){
                JSONObject jsonObject = jsonArray.getJSONObject(count);
                String title = jsonObject.getString("title");
                String avt_url =jsonObject.getString("thumb");
                VideoCategory category = new VideoCategory(title,avt_url);
                currentList.add(category);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentList;
    }

    private class CategoryHTTP extends AsyncTask<String , Void, Void>{
        String url;

        public CategoryHTTP(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            tv_loading_category.setText(getString(R.string.loading));
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(String... strings) {
            json = getJsonFromWeb(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(json != null){
                tv_loading_category.setText("");
                videoCategoryList = getListCategory(json);
                CategoryAdapter categoryAdapter = new CategoryAdapter(videoCategoryList, getContext(), new CategoryClick() {
                    @Override
                    public void onCategoryClick(String category) {
                        Intent intent = new Intent(getContext(), ListVideoActivity.class);
                        intent.putExtra("title", "null");
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(categoryAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            }else{
                tv_loading_category.setText(getString(R.string.disconnect));
            }
        }
    }
}
