package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.ourincheon.wazap.Retrofit.ApplierData;
import com.ourincheon.wazap.Retrofit.Appliers;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.Contests;

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

public class ApplierList extends AppCompatActivity {
    public static Context mContext;
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    Appliers appliers;
    ArrayList<ApplierData> applier_list;
    String num,access_token;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applier_list);

        mContext = this;

        Intent intent = getIntent();
        System.out.println(intent.getExtras().getString("id"));
        num = intent.getExtras().getString("id");

        mListView = (ListView) findViewById(R.id.applierlistView);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");
        System.out.println(pref.getString("profile_img", ""));

        applier_list = new ArrayList<ApplierData>();

        loadApplier(num,access_token);


        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplier(num, access_token);
    }

    void loadApplier(String num, String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


        Call<Appliers> call = service.getApplierlist(num, access_token);
        call.enqueue(new Callback<Appliers>() {
            @Override
            public void onResponse(Response<Appliers> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    appliers = response.body();

                    String result = new Gson().toJson(appliers);
                    Log.d("SUCESS-----", result);

                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();
                        System.out.println(count);
                        mAdapter = new ListViewAdapter(mContext);
                        for (int i = 0; i < count; i++) {
                            System.out.println("===="+Integer.parseInt(jsonArr.getJSONObject(i).getString("is_check")));
                            mAdapter.addItem(jsonArr.getJSONObject(i).getString("profile_img"),
                                    jsonArr.getJSONObject(i).getString("username"),
                                    jsonArr.getJSONObject(i).getString("app_users_id"),
                                    Integer.parseInt(jsonArr.getJSONObject(i).getString("applies_id")),
                                            Integer.parseInt(jsonArr.getJSONObject(i).getString("is_check")));
                        }
                        mListView.setAdapter(mAdapter);

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

        public ImageView aImage;
        public TextView aName;
        public Button aPBtn;
        public Button aABtn;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ApplierData> mListData = new ArrayList<ApplierData>();

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

        public void addItem(String img, String name,String id,int applies, int is_check ){
            ApplierData addInfo = null;
            addInfo = new ApplierData();
            try {
                String thumb = URLDecoder.decode(img, "EUC_KR");
                addInfo.setProfile_img(thumb);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            addInfo.setUsername(name);
            addInfo.setApp_users_id(id);
            addInfo.setApplies_id(applies);
            addInfo.setIs_check(is_check);

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
            final ViewHolder holder;
            final Context context = parent.getContext();

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.applier_item, null);

                holder.aName = (TextView) convertView.findViewById(R.id.aName);
                holder.aImage = (ImageView) convertView.findViewById(R.id.aImage);
                holder.aPBtn = (Button) convertView.findViewById(R.id.aPBtn);
                holder.aABtn = (Button) convertView.findViewById(R.id.aABtn);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            final ApplierData mData = mListData.get(position);


            holder.aName.setText(mData.getUsername());


        /*  if (mData.getProfile_img() != null) {
              Glide.with(mContext).load(mData.getProfile_img()).error(R.drawable.icon_user).override(150,150).crossFade().into(holder.aImage);
          }else{
              holder.aImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_user));
          }*/

            try {
                String thumb = URLDecoder.decode(mData.getProfile_img(), "EUC_KR");
                //Glide.with(mContext).load(thumb).error(R.drawable.icon_user).override(50,50).crossFade().into(jImg);
                Glide.with(mContext).load(thumb).asBitmap().centerCrop().error(R.drawable.icon_user).into(new BitmapImageViewTarget(holder.aImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.aImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            // 지원자의 수락여부에 맞게 표시
            if(mData.getIs_check()==1)
                holder.aABtn.setBackgroundResource(R.drawable.accept_button_on);
            else
                holder.aABtn.setBackgroundResource(R.drawable.accept_button_off);

            // 프로필 보기페이지로 이동
            holder.aPBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ApplierList.this, showApplier.class);
                    intent.putExtra("thumbnail", mData.getProfile_img());
                    intent.putExtra("user_id", mData.getApp_users_id());
                    intent.putExtra("applies_id", String.valueOf(mData.getApplies_id()));
                    intent.putExtra("contest_id", num);
                    intent.putExtra("is_ok", mData.getIs_check());
                    startActivity(intent);
                }
            });

          /*  holder.aPBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ApplierList.this, showMypageActivity.class);
                    intent.putExtra("user_id", mData.getApp_users_id());
                    intent.putExtra("flag",3);
                    startActivity(intent);
                }
            });
*/
            return convertView;
        }
    }
}


