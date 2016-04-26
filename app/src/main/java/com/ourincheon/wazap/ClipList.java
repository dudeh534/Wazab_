package com.ourincheon.wazap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.Retrofit.Alarms;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.Contests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hsue on 16. 2. 25.
 */
public class ClipList extends AppCompatActivity {
    Context context;
    ScrollView scrollView;
    private ListView mListView = null;
    private ListView mListView2 = null;
    private ListViewAdapter mAdapter = null;
    private Not_ListViewAdapter  not_listAdapter = null;
    Contests clips;
    ArrayList<ContestData> clip_list;
    int count, posi;
    String[] id_list;
    AlertDialog dialog;
    String access_token,contest_id;
    Button jBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_list);

        context = this;

        mListView = (ListView) findViewById(R.id.cliplistView);
        mListView2 = (ListView)findViewById(R.id.listView1);

        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");

        clip_list = new ArrayList<ContestData>();

        loadClip(access_token);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("찜 목록 지우기").setMessage("해당 스크랩을 지우시겠습니까?")
                .setCancelable(true).setPositiveButton("지우기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                System.out.println("----------------------"+posi);
                deleteClip(id_list[posi]);
                loadClip(access_token);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog = builder.create();

       /* mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ClipList.this, "취소", Toast.LENGTH_SHORT).show();
                posi = position;
                dialog.show();
                return false;
            }
        });*/

        /**** 상세보기 ****/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ContestData mData = mAdapter.mListData.get(position);
                // Toast.makeText(AlarmList.this, mData.msg_url, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClipList.this, JoinActivity.class);
                intent.putExtra("id",String.valueOf(mData.getContests_id()));
                startActivity(intent);
            }
        });


        jBefore = (Button) findViewById(R.id.cBefore);
        jBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new ListViewAdapter(this);
        not_listAdapter = new Not_ListViewAdapter(this);

        mListView.setAdapter(mAdapter);
        mListView2.setAdapter(not_listAdapter);
    }

    // 리스트 크기조정용
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadClip(access_token);
    }

    void applyContest(String num, String access_token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("-------------------" + access_token);
        Call<LinkedTreeMap> call = service.applyContests(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "신청되었습니다.", Toast.LENGTH_SHORT).show();
                        onResume();
                        //deleteClip(contest_id);
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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

    void deleteClip(String contest_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


        Call<LinkedTreeMap> call = service.delClip(contest_id, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "스크랩 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        onResume();
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "스크랩취소 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    void loadClip(String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


        Call<Contests> call = service.getCliplist(access_token,200);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    clips = response.body();

                   String result = new Gson().toJson(clips);
                    Log.d("SUCESS-----", result);

                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();
                        id_list = new String[count];
                        System.out.println(count);

                        mAdapter = new ListViewAdapter(context);
                        not_listAdapter = new Not_ListViewAdapter(context);

                        for (int i = 0; i < count; i++) {
                            System.out.println("------------------"+Integer.parseInt(jsonArr.getJSONObject(i).getString("is_finish")));
                            if(Integer.parseInt(jsonArr.getJSONObject(i).getString("is_finish")) == 0) {
                                id_list[i]= jsonArr.getJSONObject(i).getString("contests_id");
                                mAdapter.addItem(jsonArr.getJSONObject(i).getString("title"),
                                        jsonArr.getJSONObject(i).getString("period"),
                                        jsonArr.getJSONObject(i).getString("categories"),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("contests_id")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("recruitment")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("is_apply")));
                            }
                            else
                            {
                                not_listAdapter.addItem(jsonArr.getJSONObject(i).getString("title"),
                                        jsonArr.getJSONObject(i).getString("period"),
                                        jsonArr.getJSONObject(i).getString("categories"),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("contests_id")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("recruitment")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("is_apply")));
                            }
                          }
                        mListView.setAdapter(mAdapter);
                        mListView2.setAdapter(not_listAdapter);

                        setListViewHeightBasedOnChildren(mListView);
                        setListViewHeightBasedOnChildren(mListView2);
                    } catch (JSONException e) {
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



    private class ViewHolder {
       // public ImageView mIcon;

        public TextView Dday;
        public TextView cTitle;
        public TextView Cate1;
        public TextView Cate2;
        public ImageView c1,c2;
        public TextView Member;
        Button Join;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ContestData> mListData = new ArrayList<ContestData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String title,String period, String categories, int id, int member,int is_apply ){
            ContestData addInfo = null;
            addInfo = new ContestData();
            addInfo.setTitle(title);
            String[] parts = period.split("T");
            addInfo.setPeriod(parts[0]);
            addInfo.setCategories(categories);
            addInfo.setContests_id(id);
            addInfo.setRecruitment(member);
            addInfo.setIs_apply(is_apply);

            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.clip_item, null);

                holder.Dday = (TextView) convertView.findViewById(R.id.cDday);
                holder.cTitle = (TextView) convertView.findViewById(R.id.cTitle);
                holder.Cate1 = (TextView) convertView.findViewById(R.id.cCate1);
                holder.Member = (TextView) convertView.findViewById(R.id.cMember);
                holder.Cate2 = (TextView) convertView.findViewById(R.id.cCate2);

                holder.c1 = (ImageView) convertView.findViewById(R.id.image1);
                holder.c2 = (ImageView) convertView.findViewById(R.id.image2);

                holder.Join = (Button) convertView.findViewById(R.id.cJoin);

                holder.Join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContestData mData = mListData.get(position);
                        contest_id = String.valueOf(mData.getContests_id());
                       // applyContest(contest_id, access_token);
                        posi = position;
                        dialog.show();
                    }
                });

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ContestData mData = mListData.get(position);

            Dday day = new Dday();
            holder.Dday.setText("D - "+day.dday(mData.getPeriod()));

            holder.cTitle.setText(mData.getTitle());

            //holder.Cate1.setText(mData.getCategories());

            holder.Member.setText("모집인원 " + mData.getRecruitment() + "명");


            //// 카테고리 명에 맞는 이미지 출력 ////
            String[] temp = mData.getCategories().split(" ");
            System.out.println(temp.length);

            if(temp.length == 1 ) {
                holder.Cate1.setText(temp[0]);

                if(temp[0].equals("사진/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_video);
                else if(temp[0].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_design);
                else if(temp[0].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_it);
                else if(temp[0].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_idea);
                else if(temp[0].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_scenario);

                holder.Cate2.setText(" ");
                holder.c2.setVisibility(View.INVISIBLE);
            }
            else if(temp.length > 2) {
                holder.Cate1.setText(temp[0]);

                if(temp[0].equals("사진/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_video);
                else if(temp[0].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_design);
                else if(temp[0].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_it);
                else if(temp[0].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_idea);
                else if(temp[0].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_icon_scenario);

                holder.Cate2.setText(temp[2]);
                holder.c2.setVisibility(View.VISIBLE);
                if(temp[2].equals("사진/UCC"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_video);
                else if(temp[2].equals("디자인"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_design);
                else if(temp[2].equals("게임/소프트웨어"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_it);
                else if(temp[2].equals("해외"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_idea);
                else if(temp[2].equals("광고/아이디어/마케팅"))
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_marketing);
                else
                    holder.c2.setBackgroundResource(R.drawable.detail_icon_scenario);
            }

            return convertView;
        }
    }


    //*** 마감된 리스트 ***//
    private class Not_ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ContestData> mListData = new ArrayList<ContestData>();

        public Not_ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String title,String period, String categories, int id, int member,int is_apply ){
            ContestData addInfo = null;
            addInfo = new ContestData();
            addInfo.setTitle(title);
            String[] parts = period.split("T");
            addInfo.setPeriod(parts[0]);
            addInfo.setCategories(categories);
            addInfo.setContests_id(id);
            addInfo.setRecruitment(member);
            addInfo.setIs_apply(is_apply);

            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.not_clip_item, null);

                holder.Dday = (TextView) convertView.findViewById(R.id.ncDday);
                holder.cTitle = (TextView) convertView.findViewById(R.id.ncTitle);
                holder.Cate1 = (TextView) convertView.findViewById(R.id.ncCate1);
                holder.Member = (TextView) convertView.findViewById(R.id.ncMember);
                holder.Cate2 = (TextView) convertView.findViewById(R.id.ncCate2);

                holder.c1 = (ImageView) convertView.findViewById(R.id.img1);
                holder.c2 = (ImageView) convertView.findViewById(R.id.img2);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ContestData mData = mListData.get(position);

            holder.cTitle.setText(mData.getTitle());

            //// 카테고리 명에 맞는 이미지 출력 ////
            String[] temp = mData.getCategories().split(" ");
            System.out.println(temp.length);

            if(temp.length == 1 ) {
                holder.Cate1.setText(temp[0]);

                if(temp[0].equals("사진/영상/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_video);
                else if(temp[0].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_design);
                else if(temp[0].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_it);
                else if(temp[0].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_idea);
                else if(temp[0].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_scenario);

                holder.Cate2.setText(" ");
                holder.c2.setVisibility(View.INVISIBLE);
            }
            else if(temp.length > 2) {
                holder.Cate1.setText(temp[0]);

                if(temp[0].equals("사진/영상/UCC"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_video);
                else if(temp[0].equals("디자인"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_design);
                else if(temp[0].equals("게임/소프트웨어"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_it);
                else if(temp[0].equals("해외"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_idea);
                else if(temp[0].equals("광고/아이디어/마케팅"))
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_marketing);
                else
                    holder.c1.setBackgroundResource(R.drawable.detail_disable_scenario);

                holder.Cate2.setText(temp[2]);
                holder.c2.setVisibility(View.VISIBLE);
                if(temp[2].equals("사진/영상/UCC"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_video);
                else if(temp[2].equals("디자인"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_design);
                else if(temp[2].equals("게임/소프트웨어"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_it);
                else if(temp[2].equals("해외"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_idea);
                else if(temp[2].equals("광고/아이디어/마케팅"))
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_marketing);
                else
                    holder.c2.setBackgroundResource(R.drawable.detail_disable_scenario);
            }

            holder.Member.setText("모집인원 " + mData.getRecruitment() + "명");

            return convertView;
        }
    }

}

