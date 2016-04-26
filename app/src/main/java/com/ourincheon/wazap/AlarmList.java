package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.Retrofit.Alarms;
import com.ourincheon.wazap.Retrofit.Contests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hsue on 16. 2. 25.
 */

public class AlarmList extends AppCompatActivity {
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    Alarms alarms;
    ArrayList<AlarmData> alarm_list;
    int count;
    AlarmData con;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        mListView = (ListView) findViewById(R.id.aList);



        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String access_token = pref.getString("access_token", "");

        alarm_list = new ArrayList<AlarmData>();

        loadAlarm(access_token);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                AlarmData mData = mAdapter.mListData.get(position);
                Toast.makeText(AlarmList.this, mData.msg_url, Toast.LENGTH_SHORT).show();
                String temp = mData.msg_url.substring(0,14);

                System.out.println(temp);
                if(temp.equals("/contests/list"))
                    intent = new Intent(AlarmList.this, ContestList.class);
                else if(temp.equals("/contests/appl"))
                    intent = new Intent(AlarmList.this, ApplyList.class);
                startActivity(intent);

            }
        });


        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    void loadAlarm(String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);



        Call<Alarms> call = service.getAlarmlist(access_token,  100);
        call.enqueue(new Callback<Alarms>() {
            @Override
            public void onResponse(Response<Alarms> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    alarms = response.body();


                    String result = new Gson().toJson(alarms);
                    Log.d("SUCESS-----", result);

                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();
                        System.out.println(count);
                        for (int i = 0; i < count; i++) {

                            mAdapter.addItem(jsonArr.getJSONObject(i).getString("msg_url"),
                                    jsonArr.getJSONObject(i).getString("msg"), jsonArr.getJSONObject(i).getString("alramdate"),
                                    jsonArr.getJSONObject(i).getString("profile_img"),
                                    Integer.parseInt(jsonArr.getJSONObject(i).getString("alram_id")),
                                    Integer.parseInt(jsonArr.getJSONObject(i).getString("is_check")),
                                    jsonArr.getJSONObject(i).getString("username"));
                        }
                        mAdapter.notifyDataSetChanged();
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
        public ImageView mIcon;

        public TextView mText;

        public TextView mDate;
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

        public void addItem(String msg_url, String msg, String date, String img ,int alarm_id, int is_check, String username){
            AlarmData addInfo = null;
            addInfo = new AlarmData();
            addInfo.msg_url = msg_url;
            addInfo.msg = msg;
            try {
                String thumb = URLDecoder.decode(img, "EUC_KR");
                addInfo.setProfile_img(thumb);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] parts = date.split("T");
            addInfo.alramdate=parts[0];
            addInfo.alram_id = alarm_id;
            addInfo.is_check = is_check;
            addInfo.username = username;

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
                convertView = inflater.inflate(R.layout.listview_item, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mText = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            AlarmData mData = mListData.get(position);

            if (mData.getProfile_img() != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(mData.getProfile_img()).error(R.drawable.icon_user).override(150,150).crossFade().into(holder.mIcon);
                //       ThumbnailImage thumb = new ThumbnailImage(mData.getProfile_img(), holder.mIcon);
                //      thumb.execute();
            }else{
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_user));
            }


            holder.mText.setText(mData.username + mData.msg);
            holder.mDate.setText(mData.alramdate);

            return convertView;
        }
    }
}

