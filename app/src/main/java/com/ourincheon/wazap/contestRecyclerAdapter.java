package com.ourincheon.wazap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ourincheon.wazap.Retrofit.WeeklyData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sue on 2016-03-19.
 */
public class contestRecyclerAdapter extends RecyclerView.Adapter<contestRecyclerAdapter.ViewHolder>
{
        Context context;
        List<Recycler_contestItem> items;
        int item_layout;

        public contestRecyclerAdapter( Context context, List<Recycler_contestItem> items, int item_layout) {
            this.context = context;
            this.items = items;
            this.item_layout = item_layout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_contest, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Recycler_contestItem item = items.get(position);

            holder.title.setText(item.getTitle());
            holder.text_con.setText(item.getHost());
            holder.dday.setText(item.getDday());
            holder.date.setText(item.getDate());

            Glide.with(context).load(item.getImg()).error(R.drawable.testcontest).override(400,100).centerCrop().crossFade().into(holder.imageView);

            // 각각 카드뷰 누를 경우, 상세 공모전 보기로 이동
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, showWeeklyActivity.class);
                    intent.putExtra("Item", item);
                    context.startActivity(intent);
                }
            });

            // 카테고리별 분류
            String[] temp=item.getCategory().split(",");
            for(int j=0; j<temp.length; j++) {
                if(temp[j].trim().equals("광고/아이디어/마케팅"))
                    holder.category.setBackgroundResource(R.drawable.detail_icon_marketing);
                else if(temp[j].trim().equals("디자인/플래"))
                    holder.category.setBackgroundResource(R.drawable.detail_icon_design);
                else if(temp[j].trim().equals("사진/영상/UCC"))
                    holder.category.setBackgroundResource(R.drawable.detail_icon_video);
                else if(temp[j].trim().equals("게임/소프트웨어"))
                    holder.category.setBackgroundResource(R.drawable.detail_icon_it);
                else if(temp[j].trim().equals("해외"))
                    holder.category.setBackgroundResource(R.drawable.detail_icon_idea);
                else
                    holder.category.setBackgroundResource(R.drawable.detail_icon_scenario);
            }
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView title, text_con, dday, date;
            CardView cardview;
            ImageView imageView,category;

            public ViewHolder(View itemView) {
                super(itemView);
                text_con = (TextView) itemView.findViewById(R.id.text_con);
                dday = (TextView) itemView.findViewById(R.id.dday);
                title = (TextView) itemView.findViewById(R.id.title_con);
                date = (TextView) itemView.findViewById(R.id.date);
                cardview = (CardView) itemView.findViewById(R.id.cardView);
                imageView = (ImageView) itemView.findViewById(R.id.contest_image);
                category = (ImageView) itemView.findViewById(R.id.category_image);
            }
        }
    }