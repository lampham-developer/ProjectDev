package com.example.helloworld.Rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Entity.Define;
import com.example.helloworld.R;
import com.example.helloworld.SQL.DatabaseHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RssObjectAdapter extends RecyclerView.Adapter<RssObjectAdapter.ViewHolder> {
    List<RssObject> objectList;
    IRssItemClick itemClick;
    Context context;
    DatabaseHandler databaseHandler;
    private final String saved_string = "Saved";
    private final String save_string = "Save";
    private final String remove_string = "Remove";
    private final String removed_string = "Removed";

    public RssObjectAdapter(List<RssObject> objectList, IRssItemClick itemClick, Context context, DatabaseHandler databaseHandler) {
        this.objectList = objectList;
        this.itemClick = itemClick;
        this.context = context;
        this.databaseHandler = databaseHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rss_item_view, parent, false);
        RssObjectAdapter.ViewHolder viewHolder = new RssObjectAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RssObject rssObject = objectList.get(position);
        if (rssObject.getTitle() != null) {
            holder.tv_item_title.setText(rssObject.getTitle());
        } else holder.tv_item_title.setVisibility(View.GONE);

        if (rssObject.getDes() != null) {
            holder.tv_item_des.setText(rssObject.getDes());
        } else holder.tv_item_des.setVisibility(View.GONE);

        if (rssObject.getDate() != null) {
            holder.tv_item_date.setText(rssObject.getDate());
        } else holder.tv_item_date.setVisibility(View.GONE);

        try {
            if (rssObject.getThumb() != null) {
                Picasso.with(context).load(rssObject.getThumb()).into(holder.iv_item_img);
            } else holder.iv_item_img.setImageResource(R.drawable.error_image);
        } catch (Exception e) {
            holder.iv_item_img.setImageResource(R.drawable.error_image);
        }
//        try {
//            if (rssObject.getThumb() != null) {
//                Picasso.with(context).load(rssObject.getThumb()).into(holder.iv_item_img);
//            } else holder.iv_item_img.setVisibility(View.GONE);
//        } catch (Exception e) {
//            holder.iv_item_img.setVisibility(View.GONE);
//        }
//        holder.layout_item_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                itemClick.onClick(rssObject);
//            }
//        });

        holder.tv_item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(rssObject);
            }
        });

        holder.iv_item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(rssObject);
            }
        });

        holder.tv_item_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClick(rssObject);
            }
        });
        holder.iv_option_rss_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean contain = databaseHandler.isContaiNews(rssObject.getLink());
                if (contain){
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.inflate(R.menu.menu_rss_remove);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.nav_remove_rss:
                                    Toast.makeText(context, removed_string, Toast.LENGTH_SHORT).show();
                                    databaseHandler.removeRss(rssObject);
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                } else {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.inflate(R.menu.menu_rss_option);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.nav_save_rss:
                                    Toast.makeText(context, saved_string, Toast.LENGTH_SHORT).show();
                                    saveRssObject(rssObject);
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }

            }
        });
        itemClick.onOptionClick(rssObject);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout_item_view;
        ImageView iv_item_img, iv_option_rss_item;
        TextView tv_item_title, tv_item_des, tv_item_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_item_view = itemView.findViewById(R.id.layout_item_view);
            iv_item_img = itemView.findViewById(R.id.iv_item_img);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_des = itemView.findViewById(R.id.tv_item_des);
            tv_item_date = itemView.findViewById(R.id.tv_item_date);
            iv_option_rss_item = itemView.findViewById(R.id.iv_option_rss_item);

        }
    }

    private void saveRssObject(RssObject object) {
        databaseHandler.addNews(object, Define.TABLE_SAVED_NEWS_NAME, Define.LIMIT_SAVED_NEWS);
    }

}
