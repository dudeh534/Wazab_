package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.ourincheon.wazap.Retrofit.AlarmData;
import com.ourincheon.wazap.Retrofit.AlarmRead;
import com.ourincheon.wazap.Retrofit.Alarms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class newAlarmList extends AppCompatActivity {
    Context context;
    ScrollView scrollView;
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    Alarms alarms;
    ArrayList<AlarmData> alarm_list;
    int count;
    Intent intent;
    String access_token;
    Button jBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm_list);

        context = this;
        mListView = (ListView) findViewById(R.id.alistView);
        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");
        Log.d("AlarmList", "access token: "+access_token);

        alarm_list = new ArrayList<AlarmData>();

//        loadAlarm(access_token);            // 서버에서 정보 받아오기

        // 알람 목록 중 선택했을 경우
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                AlarmData mData = mAdapter.mListData.get(position);
                String temp = mData.getMsg_url().substring(0, 14);
                // 알림 읽기 처리 후 액티비티 이동
                Log.d("TEST", mData.getAlram_id()+" , "+access_token);
                readAlarm(access_token, mData.getAlram_id(), temp);
            }
        });

        // 뒤로가는 버튼 누를 경우
        jBefore = (Button) findViewById(R.id.aBefore);
        jBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlarm(access_token);
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


    //*** 서버에서 알람목록 가져오기 ***//
    void loadAlarm(String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<Alarms> call = service.getAlarmlist(access_token, 50);
        call.enqueue(new Callback<Alarms>() {
            @Override
            public void onResponse(Response<Alarms> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    alarms = response.body();

                    String result = new Gson().toJson(alarms);
                    //Log.d("SUCESS-----", result);

                    // adapter에 data추가
                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();

                        mAdapter = new ListViewAdapter(context);
                        System.out.println(count);
                        for (int i = 0; i < count; i++) {
                            mAdapter.addItem(jsonArr.getJSONObject(i).getString("msg_url"),
                                    jsonArr.getJSONObject(i).getString("msg"), jsonArr.getJSONObject(i).getString("alramdate"),
                                    jsonArr.getJSONObject(i).getString("profile_img"),
                                    Integer.parseInt(jsonArr.getJSONObject(i).getString("alram_id")),
                                    Integer.parseInt(jsonArr.getJSONObject(i).getString("is_check")),
                                    jsonArr.getJSONObject(i).getString("username"));
                        }
                        mListView.setAdapter(mAdapter);
                        setListViewHeightBasedOnChildren(mListView);
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

    // 알람 읽기 처리
    protected void readAlarm(String access_token, int alarmId, final String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<AlarmRead> call = service.setReadAlarm(access_token, alarmId);
        call.enqueue(new Callback<AlarmRead>() {
            @Override
            public void onResponse(Response<AlarmRead> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    AlarmRead readResult = response.body();

                    // 읽기처리 성공 후 해당 액티비티로 이동
                    if (readResult.isResult()) {
                        if (url.equals("/contests/list"))
                            intent = new Intent(newAlarmList.this, ContestList.class);
                        else if (url.equals("/contests/appl"))
                            intent = new Intent(newAlarmList.this, ApplyList.class);
                        startActivity(intent);
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
        public ImageView mIcon;
        public TextView mText;
        public TextView mDate;
        public LinearLayout mList;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<AlarmData> mListData = new ArrayList<AlarmData>();

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

        //* 서버에서 받은값 저장 *//
        public void addItem(String msg_url, String msg, String date, String img ,int alarm_id, int is_check, String username){
            AlarmData addInfo = null;
            addInfo = new AlarmData();
            addInfo.setMsg_url(msg_url);
            addInfo.setMsg(msg);
            try {
                String thumb = URLDecoder.decode(img, "EUC_KR");
                addInfo.setProfile_img(thumb);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] parts = date.split("T");
            addInfo.setAlramdate(parts[0]);
            addInfo.setAlram_id(alarm_id);
            addInfo.setIs_check(is_check);
            addInfo.setUsername(username);

            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }

        //* 화면에 목록 그리기 *//
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.new_alaram_item, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
                holder.mList = (LinearLayout) convertView.findViewById(R.id.mList);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            AlarmData mData = mListData.get(position);

            // 알람이미지 불러오기
            try {
                String thumb = URLDecoder.decode(mData.getProfile_img(), "EUC_KR");
                //    Glide.with(context).load(thumb).error(R.drawable.icon_user).override(50,50).crossFade().into(jImg);
                Glide.with(context).load(thumb).asBitmap().centerCrop().error(R.drawable.icon_user).into(new BitmapImageViewTarget(holder.mIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.mIcon.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (mData.getIs_check() == 0) {
                holder.mList.setBackgroundResource(R.color.unread_item);
            }

            // 알람 메세지,날짜 불러오기
            holder.mText.setText(mData.getUsername() + mData.getMsg());
            holder.mDate.setText(mData.getAlramdate());

            return convertView;
        }
    }

}