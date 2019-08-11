package com.example.helloworld.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Entity.Video;
import com.example.helloworld.Interface.VideoClick;
import com.example.helloworld.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.Viewholder> {
    List<Video> videoList;
    Context context;
    VideoClick videoClick;


    public VideoAdapter(List<Video> videoList, Context context, VideoClick videoClick) {
        this.videoList = videoList;
        this.context = context;
        this.videoClick = videoClick;
    }

    @NonNull
    @Override
    public VideoAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.video_item_view, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.Viewholder holder, int position) {
        final Video video = videoList.get(position);
        holder.tv_item_artis.setText(video.getArtis_name());
        holder.tv_item_date.setText(video.getDate_public());
        holder.tv_item_title.setText(video.getTitle());
        Picasso.with(context).load(video.getAvt_url()).into(holder.iv_item_img);
        holder.layout_item_video_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoClick.onClick(video);
            }
        });


    }

    @Override
    public int getItemCount() {

        return videoList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView tv_item_title;
        TextView tv_item_artis;
        TextView tv_item_date;
        LinearLayout layout_item_video_view;
        ImageView iv_item_img;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tv_item_artis = itemView.findViewById(R.id.tv_item_artis);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_date = itemView.findViewById(R.id.tv_item_date);
            iv_item_img = itemView.findViewById(R.id.iv_item_img);
            layout_item_video_view = itemView.findViewById(R.id.layout_item_video_view);
        }
    }

}
