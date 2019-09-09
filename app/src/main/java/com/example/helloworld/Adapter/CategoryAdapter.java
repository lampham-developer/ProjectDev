package com.example.helloworld.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Entity.VideoCategory;
import com.example.helloworld.Interface.ICategoryClick;
import com.example.helloworld.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Viewholder> {

    List<VideoCategory> categoryList;
    Context context;
    ICategoryClick ICategoryClick;

    public CategoryAdapter(List<VideoCategory> categoryList, Context context, ICategoryClick ICategoryClick) {
        this.categoryList = categoryList;
        this.context = context;
        this.ICategoryClick = ICategoryClick;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_item, parent, false);
        CategoryAdapter.Viewholder viewholder = new CategoryAdapter.Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
            final VideoCategory videoCategory = categoryList.get(position);
            holder.tv_category_title.setText(videoCategory.getTitle());
            Picasso.with(context).load(videoCategory.getAvt_url()).into(holder.iv_category_img);
            holder.layout_category_item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ICategoryClick.onCategoryClick(videoCategory.getTitle());
                }
            });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView iv_category_img;
        TextView tv_category_title;
        RelativeLayout layout_category_item_view;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            iv_category_img = itemView.findViewById(R.id.iv_category_img);
            tv_category_title = itemView.findViewById(R.id.tv_category_title);
            layout_category_item_view = itemView.findViewById(R.id.layout_category_item_view);
        }
    }
}
