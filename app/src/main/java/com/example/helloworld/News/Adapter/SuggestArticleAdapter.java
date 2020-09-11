package com.example.helloworld.News.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.News.Entities.RssObject;
import com.example.helloworld.News.Entities.IRssItemClick;
import com.example.helloworld.R;

import java.util.List;

public class SuggestArticleAdapter extends RecyclerView.Adapter<SuggestArticleAdapter.ViewHolder> {
    List<RssObject> rssObjectList;
    Context context;
    com.example.helloworld.News.Entities.IRssItemClick IRssItemClick;

    public SuggestArticleAdapter(List<RssObject> rssObjectList, Context context, IRssItemClick IRssItemClick) {
        this.rssObjectList = rssObjectList;
        this.context = context;
        this.IRssItemClick = IRssItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rss_suggest_view, parent, false);
        SuggestArticleAdapter.ViewHolder viewHolder = new SuggestArticleAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RssObject rssObject = rssObjectList.get(position);

        holder.tv_rss_suggest_title.setText(rssObject.getTitle());
        holder.layout_rss_suggest_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IRssItemClick.onClick(rssObject);
            }
        });

    }

    @Override
    public int getItemCount() {
        return rssObjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout_rss_suggest_item;
        TextView tv_rss_suggest_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_rss_suggest_item = itemView.findViewById(R.id.layout_rss_suggest_item);
            tv_rss_suggest_title = itemView.findViewById(R.id.tv_rss_suggest_title);

        }
    }
}
