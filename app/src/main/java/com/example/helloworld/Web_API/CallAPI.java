package com.example.helloworld.Web_API;

import android.os.AsyncTask;

import com.example.helloworld.Entity.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class CallAPI  {

    public CallAPI() {
    }

    public  String getJsonFromWeb(String url){
        String result = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            InputStream inputStream = new BufferedInputStream(con.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            inputStream.close();
            result = stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Video> getListVideo(String json, String category,boolean isRandom){
        List<Video> currentList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            int count = 0;
            while (jsonArray.getJSONObject(count) != null){
                JSONObject jsonObject = jsonArray.getJSONObject(count);
                if(jsonObject.getString("category_id").equals(category)) {
                    String title = jsonObject.getString("title");
                    String artis = jsonObject.getString("artist_name");
                    String date = jsonObject.getString("date_published");
                    String avt_url = jsonObject.getString("avatar");
                    String mp4_url = jsonObject.getString("file_mp4");

                    Video video = new Video(title, date, artis, avt_url, mp4_url);
                    currentList.add(video);
                }
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(isRandom){
            List<Video> randomList = new Vector<>();
            while (randomList.size() < 5){
                int temp = new Random().nextInt(currentList.size());
                Video current = currentList.get(temp);
                if (!randomList.contains(current)){
                    randomList.add(current);
                }
            }
            return randomList;
        }

        return currentList;

    }
}
