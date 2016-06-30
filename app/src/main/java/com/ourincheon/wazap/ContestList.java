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
import android.util.SparseBooleanArray;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/*숨기는 뷰까지 만들어 놨고 이제 상세보기 눌렀을때 그 뷰가 보이도록 그리고 list set하도록
* 상세보기 눌렀을때 고정*/
public class ContestList extends AppCompatActivity {
    private final String TAG = "ContestList";

    public static Context mContext;
    public static ArrayList<ApplierData> mListData = new ArrayList<ApplierData>();
    public static HashMap<Integer,Integer> List_SIZE = new HashMap<>();
    ScrollView scrollView;
    Contests contests;
    ArrayList<ContestData> contest_list;
    int count;
    Button jBefore;
    String num,access_token, user_id;
    private ListView mListView = null;
    private ListView mListView2 = null;
    private ListViewAdapter mAdapter = null;
    private Not_ListViewAdapter not_listAdapter = null;
    private int temp = 0;
    public void setListViewHeightBasedOnChildren(ListView listView) {
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
        params.height = totalHeight + (listAdapter.getCount() - 1);
        //params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void setListViewHeightBasedOnChildren_add(ListView parentListView, ListView listView,int position) {
        ListAdapter parentListAdapter = parentListView.getAdapter();
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
        List_SIZE.put(position, mListData.size());

        temp += List_SIZE.get(position);

        ViewGroup.LayoutParams params = parentListView.getLayoutParams();
        params.height = params.height + totalHeight;
        parentListView.setLayoutParams(params);
        parentListView.requestLayout();
    }

    public void setListViewHeightBasedOnChildren_delete(ListView listView, int position) {
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
        temp -= List_SIZE.get(position);
        List_SIZE.remove(position);


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = 211 * temp + totalHeight + (listAdapter.getCount() - 1);
        //params.height = 211 * temp + totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_list);
        List_SIZE.clear();
        mListView = (ListView) findViewById(R.id.contestlistView);
        mListView2 = (ListView) findViewById(R.id.listView1);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");
        user_id = pref.getString("user_id", "");

        contest_list = new ArrayList<ContestData>();

        loadContest(user_id, access_token);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ContestData mData = mAdapter.mListData.get(position);
                // Toast.makeText(AlarmList.this, mData.msg_url, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContestList.this, MasterJoinActivity.class);
                intent.putExtra("id", String.valueOf(mData.getContests_id()));
                startActivity(intent);
            }
        });

        jBefore = (Button) findViewById(R.id.conBefore);
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

    void loadContest(String user_id, String access_token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<Contests> call = service.getContestlist(user_id, access_token);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    contests = response.body();

                    String result = new Gson().toJson(contests);
                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();
                        System.out.println(count);

                        for (int i = 0; i < count; i++) {

                            if (Integer.parseInt(jsonArr.getJSONObject(i).getString("is_finish")) == 0) {
                                mAdapter.addItem(jsonArr.getJSONObject(i).getString("title"),
                                        jsonArr.getJSONObject(i).getString("period"),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("appliers")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("recruitment")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("contests_id")),
                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("members")));
                            } else {
                                not_listAdapter.addItem(jsonArr.getJSONObject(i).getString("title"),
                                        jsonArr.getJSONObject(i).getString("period"),
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    void loadApplier(String num, String access_token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


    }

    private class ViewHolder {

        public TextView Dday;
        public TextView Title;
        public TextView Cate;
        public TextView Man;
        public TextView Member;
        public LinearLayout open_layout;
        public ListView mListView1;
        Button Detail;
        LinearLayout DetailArea;
    }

    //--------------------------------------------------------------------------------어댑터
    private class ListViewAdapter extends BaseAdapter {
        String access_token;
        int count;
        Appliers appliers;
        ArrayList<ApplierData> applier_list;
        private Context mContext = null;
        private ArrayList<ContestData> mListData = new ArrayList<ContestData>();
        private SparseBooleanArray selectItem = new SparseBooleanArray();
        private ListViewAdapter1 mAdapter1 = null;

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

        public void addItem(String title, String period, int apply, int recruit, int id, int member) {
            ContestData addInfo = null;
            addInfo = new ContestData();
            String[] parts = period.split("T");
            addInfo.setPeriod(parts[0]);
            addInfo.setAppliers(apply);
            addInfo.setRecruitment(recruit);
            addInfo.setContests_id(id);
            addInfo.setMembers(member);
            addInfo.setTitle(title);

            mListData.add(addInfo);

        }

        public void changeMemberCount(int position, int count) {
            ContestData changeInfo = mListData.get(position);

            // 현재 멤버수에 +1 또는 -1 을 함
            changeInfo.setMembers(changeInfo.getMembers() + count);

            mListData.set(position, changeInfo);
            dataChange();
        }


        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contest_item, null);

                holder.Dday = (TextView) convertView.findViewById(R.id.rdday);
                holder.Title = (TextView) convertView.findViewById(R.id.rtitle);
                holder.Cate = (TextView) convertView.findViewById(R.id.rcate);
                holder.Man = (TextView) convertView.findViewById(R.id.rman);
                holder.Member = (TextView) convertView.findViewById(R.id.rmember);
                holder.open_layout = (LinearLayout) convertView.findViewById(R.id.open_layout);
                holder.mListView1 = (ListView) convertView.findViewById(R.id.applierlistView);
                holder.DetailArea = (LinearLayout) convertView.findViewById(R.id.btnArea);
                holder.DetailArea.setSelected(selectItem.get(position, false));


                // 코드 수정할것-너무지저분
                holder.Detail = (Button) convertView.findViewById(R.id.rdetail);
                holder.Detail.setSelected(selectItem.get(position, false));
                holder.Detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                 /*       ContestData mData = mAdapter.mListData.get(position);
                        Intent intent = new Intent(ContestList.this, MasterJoinActivity.class);
                        intent.putExtra("id",String.valueOf(mData.getContests_id()));
                        startActivity(intent);
                   */

                        //여기 신청자 목록 눌렀을 때
                        if (selectItem.get(position, false)) {
                            selectItem.delete(position);
                            holder.Detail.setSelected(false);//? 이거는 왜 안돼
                            holder.open_layout.setVisibility(View.GONE);
                            setListViewHeightBasedOnChildren_delete(mListView,position);
                        } else {
                            selectItem.put(position, true);
                            holder.Detail.setSelected(true);
                            holder.open_layout.setVisibility(View.VISIBLE);
                            ContestData mData = mAdapter.mListData.get(position);
                            num = String.valueOf(mData.getContests_id());
                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                            access_token = pref.getString("access_token", "");
                            applier_list = new ArrayList<ApplierData>();
                            mAdapter1 = new ListViewAdapter1(ContestList.this);
                            //-----------------------------------
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
                                            if(count==0)
                                                Toast.makeText(ContestList.this, "신청자가 없습니다.", Toast.LENGTH_LONG).show();
                                            mAdapter1 = new ListViewAdapter1(mContext);
                                            for (int i = 0; i < count; i++) {
                                                mAdapter1.addItem(jsonArr.getJSONObject(i).getString("profile_img"),
                                                        jsonArr.getJSONObject(i).getString("username"),
                                                        jsonArr.getJSONObject(i).getString("app_users_id"),
                                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("applies_id")),
                                                        Integer.parseInt(jsonArr.getJSONObject(i).getString("is_check")),
                                                        position);
                                            }
                                            // TODO 여기가 세개이상인데 리스트뷰 크기가 3개이상 안늘어남
                                            Log.d("TEST", "신청자 목록 길이: "+mAdapter1.getCount());
                                            holder.mListView1.setAdapter(mAdapter1);
                                            holder.mListView1.setDivider(null);
                                            holder.mListView1.setDividerHeight(0);
                                            setListViewHeightBasedOnChildren(holder.mListView1);
                                            setListViewHeightBasedOnChildren_add(mListView, holder.mListView1, position);
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
                    }
                });



                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }



            ContestData mData = mListData.get(position);

            Dday day = new Dday();
            holder.Dday.setText("D - " + day.dday(mData.getPeriod()));
            holder.Title.setText(mData.getTitle());
            holder.Cate.setText("모집인원 " + String.valueOf(mData.getRecruitment()) + "명");
            holder.Man.setText("신청인원 " + mData.getAppliers() + "명");
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

        public void addItem(String title, String period, int apply, int recruit, int id, int member) {
            ContestData addInfo = null;
            addInfo = new ContestData();
            String[] parts = period.split("T");
            addInfo.setPeriod(parts[0]);
            addInfo.setAppliers(apply);
            addInfo.setRecruitment(recruit);
            addInfo.setContests_id(id);
            addInfo.setMembers(member);
            addInfo.setTitle(title);

            mListData.add(addInfo);

        }

        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void dataChange() {
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
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ContestData mData = mListData.get(position);

            holder.Title.setText(mData.getTitle());
            holder.Cate.setText("모집인원 " + String.valueOf(mData.getRecruitment()) + "명");
            holder.Man.setText("신청인원 " + mData.getAppliers() + "명");
            holder.Member.setText("확정인원 " + mData.getMembers() + "명");

            return convertView;
        }
    }

    private class ViewHolder1 {

        public ImageView aImage;
        public TextView aName;
        public Button aPBtn;
        public Button aABtn;
    }

    private class ListViewAdapter1 extends BaseAdapter {
        private Context mContext = null;
        private int parentPosition;

        public ListViewAdapter1(Context mContext) {
            super();
            this.mContext = mContext;
            mListData.clear();
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

        public void addItem(String img, String name, String id, int applies, int is_check, int parentPosition) {
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

            this.parentPosition = parentPosition;
            mListData.add(addInfo);
        }

        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            final ViewHolder1 holder;

            if (convertView == null) {
                holder = new ViewHolder1();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.applier_item, null);

                holder.aName = (TextView) convertView.findViewById(R.id.aName);
                holder.aImage = (ImageView) convertView.findViewById(R.id.aImage);
                holder.aPBtn = (Button) convertView.findViewById(R.id.aPBtn);
                holder.aABtn = (Button) convertView.findViewById(R.id.aABtn);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder1) convertView.getTag();
            }

            final ApplierData mData = mListData.get(position);


            holder.aName.setText(mData.getUsername());


           /* if (mData.getProfile_img() != null) {
                Glide.with(mContext).load(mData.getProfile_img()).error(R.drawable.icon_user).override(150, 150).crossFade().into(holder.aImage);
            } else {
                holder.aImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_user));
            }
*/
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
            if (mData.getIs_check() == 1)
                holder.aABtn.setBackgroundResource(R.drawable.accept_button_on);
            else
                holder.aABtn.setBackgroundResource(R.drawable.accept_button_off);

            // 프로필 보기페이지로 이동
            holder.aPBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ContestList.this, showApplier.class);
                    intent.putExtra("thumbnail", mData.getProfile_img());
                    intent.putExtra("user_id", mData.getApp_users_id());
                    intent.putExtra("applies_id", String.valueOf(mData.getApplies_id()));
                    intent.putExtra("contest_id", num);
                    intent.putExtra("is_ok", mData.getIs_check());
                    startActivity(intent);
                }
            });

            // 수락 버튼 눌렀을 경우
            holder.aABtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //*** 서버로 멤버 변경에 대해 요청하기 ***//
                    if (mData.getIs_check() == 0){
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://come.n.get.us.to/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        WazapService service = retrofit.create(WazapService.class);

                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        String access_token = pref.getString("access_token", "");

                        Call<LinkedTreeMap> call = service.changeMember(num, String.valueOf(mData.getApplies_id()), access_token);
                        call.enqueue(new Callback<LinkedTreeMap>() {
                            @Override
                            public void onResponse(Response<LinkedTreeMap> response) {
                                if (response.isSuccess() && response.body() != null) {

                                    LinkedTreeMap temp = response.body();

                                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                                    String msg = temp.get("msg").toString();

                                    if (result) {
                                        Log.d("저장 결과: ", msg);
                                        Toast.makeText(getApplicationContext(), "멤버 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                                        mData.setIs_check(1);
                                        holder.aABtn.setBackgroundResource(R.drawable.accept_button_on);

                                        // TODO mAdapter 변경후 ListView 리프레쉬 필요
                                        mAdapter.changeMemberCount(parentPosition, 1);
                                    } else {
                                        Log.d("저장 실패: ", msg);
                                        Toast.makeText(getApplicationContext(), "멤버 변경 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                }
            });

            return convertView;
        }
    }

}

