package com.ourincheon.wazap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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

public class ApplyList extends AppCompatActivity {
    Context context;
    ScrollView scrollView;
    private ListView mListView = null;
    private ListView mListView2 = null;
    private ListViewAdapter mAdapter = null;
    private Not_ListViewAdapter  not_listAdapter = null;
    Contests applies;
    ArrayList<ContestData> apply_list;
    int count, posi;
    String[] cont_id,apply_id;
    AlertDialog dialog;
    String access_token;
    String contest_id;
    Button jBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_list);

        context = this;

        mListView = (ListView) findViewById(R.id.listView);
        mListView2 = (ListView)findViewById(R.id.listView1);

        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");

        apply_list = new ArrayList<ContestData>();

        loadApply(access_token);

        // 롱 터치시, 삭제
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("신청 목록 지우기").setMessage("해당 신청공모전을 지우시겠습니까?")
                .setCancelable(true).setPositiveButton("지우기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("----------------------"+posi);
                ContestData mData = mAdapter.mListData.get(posi);
                contest_id = String.valueOf(mData.getContests_id());
                deleteApply(contest_id,apply_id[posi]);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog = builder.create();

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posi = position;
                dialog.show();
                return false;
            }
        });

        // 터치 시, 상세페이지보기로 이동
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ContestData mData = mAdapter.mListData.get(position);
                Intent intent = new Intent(ApplyList.this, JoinActivity.class);
                intent.putExtra("id",String.valueOf(mData.getContests_id()));
                startActivity(intent);
            }
        });

        // 이전버튼 누를시, 창닫힘
        jBefore = (Button) findViewById(R.id.aBefore);
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


    //*** 서버에 신청취소 요청 ***//
    void deleteApply(String contest, String apply)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


        Call<LinkedTreeMap> call = service.delApply(contest, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "신청 취소되었습니다.", Toast.LENGTH_LONG).show();
                        onResume();
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "신청취소 안됬습니다.다시 시도해주세요.", Toast.LENGTH_LONG).show();
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
    protected void onResume() {
        super.onResume();
        loadApply(access_token);
    }

    //*** 레이아웃 그려줄때 사용 -높이계산용 ***//
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

    //*** 서버에서 신청목록 불러오기 ***//
    void loadApply(String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<Contests> call = service.getAppplylist(access_token, 10, 10);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    applies = response.body();

                    String result = new Gson().toJson(applies);
                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();
                        cont_id = new String[count];
                        apply_id = new String[count];

                        mAdapter = new ListViewAdapter(context);
                        not_listAdapter = new Not_ListViewAdapter(context);
                        for (int i = 0; i < count; i++) {
                            if(Integer.parseInt(jsonArr.getJSONObject(i).getString("is_finish")) == 0) {
                                cont_id[i] = jsonArr.getJSONObject(i).getString("contests_id");
                                apply_id[i] = jsonArr.getJSONObject(i).getString("applies_id");
                                // 마감안된 리스트 정보 받아오기
                                mAdapter.addItem(jsonArr.getJSONObject(i).getString("title"),
                                        jsonArr.getJSONObject(i).getString("period"),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("applies_id")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("appliers")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("recruitment")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("contests_id")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("members")));
                            }else{
                                // 마감된 리스트 정보 받아오기
                                not_listAdapter.addItem(jsonArr.getJSONObject(i).getString("title"),
                                        jsonArr.getJSONObject(i).getString("period"),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("applies_id")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("appliers")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("recruitment")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("contests_id")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("members")));
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
        public TextView Dday;
        public TextView Title;
        public TextView Cate;
        public TextView Man;
        public TextView Member;
        Button Detail;
        LinearLayout DetailArea;
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

        //* 마감안된 리스트에 내용추가하기 *//
        public void addItem(String title, String period, int apply_id, int apply, int recruit, int id, int member){
            ContestData addInfo = null;
            addInfo = new ContestData();
            String[] parts = period.split("T");
            addInfo.setPeriod(parts[0]);
            addInfo.setApplies_id(apply_id);
            addInfo.setAppliers(apply);
            addInfo.setRecruitment(recruit);
            addInfo.setContests_id(id);
            addInfo.setMembers(member);
            addInfo.setTitle(title);

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
                convertView = inflater.inflate(R.layout.apply_item, null);

                holder.Dday = (TextView) convertView.findViewById(R.id.dday);
                holder.Title = (TextView) convertView.findViewById(R.id.title);
                holder.Cate = (TextView) convertView.findViewById(R.id.cate);
                holder.Man = (TextView) convertView.findViewById(R.id.man);
                holder.Member = (TextView) convertView.findViewById(R.id.member);

                // 신청취소 버튼 누를경우, 서버로 취소요청
                holder.Detail = (Button) convertView.findViewById(R.id.detail);
                holder.Detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContestData mData = mAdapter.mListData.get(position);
                        contest_id = String.valueOf(mData.getContests_id());
                        deleteApply(contest_id, apply_id[position]);
                    }
                });

                // 신청취소 버튼영역 누를경우, 서버로 취소요청
                holder.DetailArea = (LinearLayout) convertView.findViewById(R.id.btnArea);
                holder.DetailArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContestData mData = mAdapter.mListData.get(position);
                        contest_id = String.valueOf(mData.getContests_id());
                        deleteApply(contest_id, apply_id[position]);
                    }
                });
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ContestData mData = mListData.get(position);

            Dday day = new Dday();
            holder.Dday.setText("D - "+day.dday(mData.getPeriod()));
            holder.Title.setText(mData.getTitle());
            holder.Cate.setText("모집인원 " + String.valueOf(mData.getRecruitment()) + "명");
            holder.Man.setText("신청인원 "+mData.getAppliers() + "명");
            holder.Member.setText("확정인원 " + mData.getMembers() + "명");

            return convertView;
        }
    }


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

        //* 마감된 리스트에 내용추가하기 *//
        public void addItem(String title, String period, int apply_id, int apply, int recruit, int id, int member){
            ContestData addInfo = null;
            addInfo = new ContestData();
            String[] parts = period.split("T");
            addInfo.setPeriod(parts[0]);
            addInfo.setApplies_id(apply_id);
            addInfo.setAppliers(apply);
            addInfo.setRecruitment(recruit);
            addInfo.setContests_id(id);
            addInfo.setMembers(member);
            addInfo.setTitle(title);

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
                convertView = inflater.inflate(R.layout.not_require_item, null);

                holder.Dday = (TextView) convertView.findViewById(R.id.dday);
                holder.Title = (TextView) convertView.findViewById(R.id.title);
                holder.Cate = (TextView) convertView.findViewById(R.id.cate);
                holder.Man = (TextView) convertView.findViewById(R.id.man);
                holder.Member = (TextView) convertView.findViewById(R.id.member);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ContestData mData = mListData.get(position);

            holder.Title.setText(mData.getTitle());
            holder.Cate.setText("모집인원 " + String.valueOf(mData.getRecruitment()) + "명");
            holder.Man.setText("신청인원 "+mData.getAppliers() + "명");
            holder.Member.setText("확정인원 " + mData.getMembers() + "명");

            return convertView;
        }
    }

}