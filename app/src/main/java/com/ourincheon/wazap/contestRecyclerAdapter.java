package com.ourincheon.wazap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Recycler_contestItem item = items.get(position);

            holder.title.setText(item.getTitle());
            holder.text_con.setText(item.getHost());
            holder.dday.setText(item.getDday());
            Log.d("Date!!!!", item.getDate());
            holder.date.setText(item.getDate());

            Glide.with(context).load(item.getImg()).error(R.drawable.testcontest).override(400,100).centerCrop().crossFade().into(holder.imageView);
            holder.imageView.setColorFilter(Color.argb(100, 0, 0, 0));

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
                Log.d("TEST", temp[j].trim());
                if(temp[j].trim().equals("광고/아이디어"))
                    holder.category.setImageResource(R.drawable.list_icon_advertising_word);
                else if(temp[j].trim().equals("건축/인테리어"))
                    holder.category.setImageResource(R.drawable.list_icon_architecture_word);
                else if(temp[j].trim().equals("예체능"))
                    holder.category.setImageResource(R.drawable.list_icon_art_word);
                else if(temp[j].trim().equals("브랜드/네이밍"))
                    holder.category.setImageResource(R.drawable.list_icon_brand_word);
                else if(temp[j].trim().equals("디자인/플래시"))
                    holder.category.setImageResource(R.drawable.list_icon_design_word);
                else if(temp[j].trim().equals("체험기/사용기"))
                    holder.category.setImageResource(R.drawable.list_icon_experience_word);
                else if(temp[j].trim().equals("게임/소프트웨어"))
                    holder.category.setImageResource(R.drawable.list_icon_game_word);
                else if(temp[j].trim().equals("문학/시나리오"))
                    holder.category.setImageResource(R.drawable.list_icon_literature_word);
                else if(temp[j].trim().equals("마케팅"))
                    holder.category.setImageResource(R.drawable.list_icon_marketing_word);
                else if(temp[j].trim().equals("사진/영상/UCC"))
                    holder.category.setImageResource(R.drawable.list_icon_photo_word);
                else if(temp[j].trim().equals("학술/논문"))
                    holder.category.setImageResource(R.drawable.list_icon_thesis_word);
                else if(temp[j].trim().equals("만화/캐릭터"))
                    holder.category.setImageResource(R.drawable.list_icon_comic_word);
                else if(temp[j].trim().equals("유사공모전"))
                    holder.category.setImageResource(R.drawable.list_icon_similar_word);
                else if(temp[j].trim().equals("해외"))
                    holder.category.setImageResource(R.drawable.list_icon_global_word);
                else
                    holder.category.setImageResource(R.drawable.list_icon_etc_word);

                // TODO 게임 추가해야함
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