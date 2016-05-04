package com.ourincheon.wazap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Youngdo on 2016-01-19.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Recycler_item> items;
    int item_layout;
    String user_id,access_token;
    Intent intent;

    public RecyclerAdapter(Context context, List<Recycler_item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;//번호별로 상세페이지
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new ViewHolder(v);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Recycler_item item = items.get(position);

        SharedPreferences pref2 = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        user_id = pref2.getString("user_id", "");
        access_token = pref2.getString("access_token", "");

        Log.d("SUCESS-----", item.getTitle());
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getName());
        holder.text.setText(item.getText());
        holder.loc.setText(item.getLoc());
        holder.recruit.setText(" / " + String.valueOf(item.getRecruit()));
        holder.member.setText(String.valueOf(item.getMember()));

        if(item.getFinish()==1) {
            holder.member.setTextColor(Color.parseColor("#b3b3b3"));
            holder.recruit.setTextColor(Color.parseColor("#b3b3b3"));
            holder.loc.setTextColor(Color.parseColor("#b3b3b3"));
            holder.title.setTextColor(Color.parseColor("#b3b3b3"));
            holder.name.setTextColor(Color.parseColor("#b3b3b3"));
            holder.text.setTextColor(Color.parseColor("#b3b3b3"));


            holder.day.setBackgroundResource(R.drawable.scrap_info_finish);
            holder.day.setText("");

            // 카드뷰 터치시-> 상세페이지로
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "이미 마감된 게시물입니다.", Toast.LENGTH_LONG).show();
                }
            });

            if(item.getClip()==0)
                holder.heart.setBackgroundResource(R.drawable.heart4);
            else
                holder.heart.setBackgroundResource(R.drawable.heart3);

            //// 카테고리 명에 맞는 이미지 출력 ////
            String[] temp = item.getCategory().split(" ");
            holder.category1.setTextColor(Color.parseColor("#b3b3b3"));
            holder.category2.setTextColor(Color.parseColor("#b3b3b3"));
            if(temp.length == 2 ) {
                holder.category1.setText(temp[1]);

                if(temp[1].equals("사진/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_video);
                else if(temp[1].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_design);
                else if(temp[1].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_it);
                else if(temp[1].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_idea);
                else if(temp[1].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_scenario);

                holder.category2.setText(" ");
                holder.c2.setVisibility(View.INVISIBLE);
            }
            else if(temp.length > 3) {
                holder.category1.setText(temp[1]);
                if(temp[1].equals("사진/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_video);
                else if(temp[1].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_design);
                else if(temp[1].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_it);
                else if(temp[1].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_idea);
                else if(temp[1].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_scenario);

                holder.category2.setText(temp[3]);
                holder.c2.setVisibility(View.VISIBLE);
                if(temp[3].equals("사진/UCC"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_video);
                else if(temp[3].equals("디자인"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_design);
                else if(temp[3].equals("게임/소프트웨어"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_it);
                else if(temp[3].equals("해외"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_idea);
                else if(temp[3].equals("광고/아이디어/마케팅"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_marketing);
                else
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_scenario);
            }
        }
        else {
            holder.member.setTextColor(Color.parseColor("#0057ff"));
            holder.recruit.setTextColor(Color.parseColor("#000000"));
            holder.loc.setTextColor(Color.parseColor("#727272"));
            holder.title.setTextColor(Color.parseColor("#000000"));
            holder.name.setTextColor(Color.parseColor("#727272"));
            holder.text.setTextColor(Color.parseColor("#727272"));

            holder.day.setBackground(null);

            holder.day.setText(item.getDay());

            // 카드뷰 터치시-> 상세페이지로
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!item.getWriter().equals(user_id))
                        intent = new Intent(context, JoinActivity.class);
                    else
                        intent = new Intent(context, MasterJoinActivity.class);

                    intent.putExtra("id", String.valueOf(item.getId()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


            if(item.getClip()==0)
                holder.heart.setBackgroundResource(R.drawable.heart1);
            else
                holder.heart.setBackgroundResource(R.drawable.heart2);

            holder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!item.getWriter().equals(user_id)) {
                        item.setClick();
                        pickContest(String.valueOf(item.getId()), access_token);

                        //make heart work well
                        if(item.getClick()%2==1 && item.getClip()==0)
                            holder.heart.setBackgroundResource(R.drawable.heart2);
                        else if(item.getClick()%2==0 && item.getClip()==0)
                            holder.heart.setBackgroundResource(R.drawable.heart1);
                        if(item.getClick()%2==1 && item.getClip()==1)
                            holder.heart.setBackgroundResource(R.drawable.heart1);
                        else if(item.getClick()%2==0 && item.getClip()==1)
                            holder.heart.setBackgroundResource(R.drawable.heart2);
                    }
                    else {
                        Toast.makeText(context, "글 작성자는 스크랩할 수 없습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            });


            //// 카테고리 명에 맞는 이미지 출력 ////
            String[] temp = item.getCategory().split(" ");
            holder.category1.setTextColor(Color.parseColor("#0057ff"));
            holder.category2.setTextColor(Color.parseColor("#0057ff"));
            if(temp.length == 2 ) {
                holder.category1.setText(temp[1]);

                if(temp[1].equals("사진/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_video);
                else if(temp[1].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_design);
                else if(temp[1].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_it);
                else if(temp[1].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_idea);
                else if(temp[1].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_scenario);

                holder.category2.setText(" ");
                holder.c2.setVisibility(View.INVISIBLE);
            }
            else if(temp.length > 3) {
                holder.category1.setText(temp[1]);
                if(temp[1].equals("사진/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_video);
                else if(temp[1].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_design);
                else if(temp[1].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_it);
                else if(temp[1].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_idea);
                else if(temp[1].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_scenario);

                holder.category2.setText(temp[3]);
                holder.c2.setVisibility(View.VISIBLE);
                if(temp[3].equals("사진/UCC"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_video);
                else if(temp[3].equals("디자인"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_design);
                else if(temp[3].equals("게임/소프트웨어"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_it);
                else if(temp[3].equals("해외"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_idea);
                else if(temp[3].equals("광고/아이디어/마케팅"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_marketing);
                else
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_scenario);
            }
        }


    }


    void pickContest(final String num, final String access_token) {
        int ret = -1;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("-------------------" + access_token);
        Call<LinkedTreeMap> call = service.clipContests(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (!msg.equals("이미 찜한 게시물 입니다.")) {
                        if (result) {
                            Log.d("저장 결과: ", msg);
                            Toast.makeText(context, "찜 되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("저장 실패: ", msg);
                            Toast.makeText(context, "찜 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        removeClip(num);
                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());

                }
            }



            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.e("Error", t.getMessage());
            }
        });
    }

    void removeClip(String num)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<LinkedTreeMap> call = service.delClip(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(context, "찜 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(context, "찜 취소안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.e("Error", t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(items == null){
            return 0;
        }else {
            return this.items.size();
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        NotoTextView title, text, name,recruit, member,loc,category1,category2,day;
        ImageView c1,c2;
        CardView cardview;
        Button heart;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (NotoTextView) itemView.findViewById(R.id.name);
            text = (NotoTextView) itemView.findViewById(R.id.text);
            loc = (NotoTextView) itemView.findViewById(R.id.loc);
            category1 = (NotoTextView) itemView.findViewById(R.id.category1);
            category2 = (NotoTextView) itemView.findViewById(R.id.category2);
            title = (NotoTextView) itemView.findViewById(R.id.title);
            recruit = (NotoTextView) itemView.findViewById(R.id.recruit);
            member = (NotoTextView) itemView.findViewById(R.id.member);
            cardview = (CardView) itemView.findViewById(R.id.cardView);
            heart = (Button) itemView.findViewById(R.id.hbutton);
            day = (NotoTextView) itemView.findViewById(R.id.day);

            c1 =(ImageView) itemView.findViewById(R.id.imageView1);
            c2 =(ImageView) itemView.findViewById(R.id.imageView2);
        }
    }

}