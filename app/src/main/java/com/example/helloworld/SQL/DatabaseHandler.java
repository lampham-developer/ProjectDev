package com.example.helloworld.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.Entity.Video;
import com.example.helloworld.Rss.RssObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "databaseManage";
    public static final int VERSION = 3;

    private Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_recently_videos_table = String.format("CREATE TABLE %s(%s TEXT, %s TEXT, %s TEXT,%s TEXT,%s TEXT PRIMARY KEY)",
                Define.TABLE_RECENTLY_VIDEOS_NAME, Define.KEY_VIDEO_TITLE, Define.KEY_VIDEO_DATE, Define.KEY_VIDEO_ARTIS, Define.KEY_VIDEO_AVT_URL, Define.KEY_VIDEO_MP4_URL);
        String create_recently_newss_table = String.format("CREATE TABLE %s(%s TEXT, %s TEXT PRIMARY KEY, %s TEXT,%s TEXT,%s TEXT)",
                Define.TABLE_RECENTLY_NEWS_NAME, Define.KEY_NEWS_TITLE, Define.KEY_NEWS_LINK, Define.KEY_NEWS_THUMB, Define.KEY_NEWS_DES, Define.KEY_NEWS_DATE);
        String create_saved_videos_table = String.format("CREATE TABLE %s(%s TEXT, %s TEXT, %s TEXT,%s TEXT,%s TEXT PRIMARY KEY)",
                Define.TABLE_SAVED_VIDEOS_NAME, Define.KEY_VIDEO_TITLE, Define.KEY_VIDEO_DATE, Define.KEY_VIDEO_ARTIS, Define.KEY_VIDEO_AVT_URL, Define.KEY_VIDEO_MP4_URL);
        String create_saved_news_table = String.format("CREATE TABLE %s(%s TEXT, %s TEXT PRIMARY KEY, %s TEXT,%s TEXT,%s TEXT)",
                Define.TABLE_SAVED_NEWS_NAME, Define.KEY_NEWS_TITLE, Define.KEY_NEWS_LINK, Define.KEY_NEWS_THUMB, Define.KEY_NEWS_DES, Define.KEY_NEWS_DATE);
        sqLiteDatabase.execSQL(create_recently_videos_table);
        sqLiteDatabase.execSQL(create_recently_newss_table);
        sqLiteDatabase.execSQL(create_saved_videos_table);
        sqLiteDatabase.execSQL(create_saved_news_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop_recently_videos_table = String.format("DROP TABLE IF EXISTS %s", Define.TABLE_RECENTLY_VIDEOS_NAME);
        String drop_recently_newss_table = String.format("DROP TABLE IF EXISTS %s", Define.TABLE_RECENTLY_NEWS_NAME);
        String drop_saved_videos_table = String.format("DROP TABLE IF EXISTS %s", Define.TABLE_SAVED_VIDEOS_NAME);
        String drop_saved_news_table = String.format("DROP TABLE IF EXISTS %s", Define.TABLE_SAVED_NEWS_NAME);

        sqLiteDatabase.execSQL(drop_recently_videos_table);
        sqLiteDatabase.execSQL(drop_recently_newss_table);
        sqLiteDatabase.execSQL(drop_saved_videos_table);
        sqLiteDatabase.execSQL(drop_saved_news_table);

        onCreate(sqLiteDatabase);
    }

    public void addVideo(Video video, String table_name, int limit) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        if (table_name.equals(Define.TABLE_RECENTLY_VIDEOS_NAME)) {
            String query = "SELECT * FROM " + table_name;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()){
                if (cursor.getCount() >= limit) {
                    String row_id = cursor.getString(cursor.getColumnIndex(Define.KEY_VIDEO_MP4_URL ));
                    sqLiteDatabase.delete(table_name, Define.KEY_VIDEO_MP4_URL + " = ?", new String[]{row_id});
                }
            }
        }
        ContentValues values = new ContentValues();
        values.put(Define.KEY_VIDEO_TITLE, video.getTitle());
        values.put(Define.KEY_VIDEO_DATE, video.getDate_public());
        values.put(Define.KEY_VIDEO_ARTIS, video.getArtis_name());
        values.put(Define.KEY_VIDEO_AVT_URL, video.getAvt_url());
        values.put(Define.KEY_VIDEO_MP4_URL, video.getMp4_url());
        
        sqLiteDatabase.delete(table_name, Define.KEY_VIDEO_MP4_URL + " = ?", new String[]{String.valueOf(video.getMp4_url())});
        sqLiteDatabase.insert(table_name, null, values);
        sqLiteDatabase.close();
    }

    public void addNews(RssObject rssObject, String table_name, int limit) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if (table_name.equals(Define.TABLE_RECENTLY_NEWS_NAME)) {
            String query = "SELECT * FROM " + table_name;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()){
                if (cursor.getCount() >= limit) {
                    String row_id = cursor.getString(cursor.getColumnIndex(Define.KEY_NEWS_LINK ));
                    sqLiteDatabase.delete(table_name, Define.KEY_NEWS_LINK + " = ?", new String[]{row_id});
                }
            }
        }

        ContentValues values = new ContentValues();
        values.put(Define.KEY_NEWS_TITLE, rssObject.getTitle());
        values.put(Define.KEY_NEWS_LINK, rssObject.getLink());
        values.put(Define.KEY_NEWS_THUMB, rssObject.getThumb());
        values.put(Define.KEY_NEWS_DES, rssObject.getDes());
        values.put(Define.KEY_NEWS_DATE, rssObject.getDate());

        sqLiteDatabase.delete(table_name, Define.KEY_NEWS_LINK + " = ?", new String[]{String.valueOf(rssObject.getLink())});
        sqLiteDatabase.insert(table_name, null, values);
        sqLiteDatabase.close();

    }

    public void removeRss(RssObject rssObject){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(Define.TABLE_SAVED_NEWS_NAME, Define.KEY_NEWS_LINK + " = ?", new String[]{rssObject.getLink()});
    }

    public boolean isContaiNews(String link){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Define.TABLE_SAVED_NEWS_NAME, null, Define.KEY_NEWS_LINK + " = ?",
                new String[]{link}, null, null, null);
        if(cursor.moveToFirst()==true) {return true;}
        else {return false;}
    }
    public boolean isContaiVideo(Video video){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Define.TABLE_SAVED_VIDEOS_NAME, null, Define.KEY_VIDEO_MP4_URL + " = ?",
                new String[]{video.getMp4_url()}, null, null, null);
        if (cursor!=null) return true;
        return false;
    }



    public List<Video> getAllVideos(String table_name) {
        List<Video> videosList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String query = "SELECT * FROM " + table_name;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            Video video = new Video(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            videosList.add(video);
            cursor.moveToPrevious();
        }

        return videosList;
    }

    public List<RssObject> getAllRssObject(String table_name) {
        List<RssObject> rssObjectList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String query = "SELECT * FROM " + table_name;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            RssObject rssObject = new RssObject(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            rssObjectList.add(rssObject);
            cursor.moveToPrevious();
        }

        return rssObjectList;
    }

}
