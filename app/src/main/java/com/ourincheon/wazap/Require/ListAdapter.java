package com.ourincheon.wazap.Require;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ourincheon.wazap.R;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.Contests;
import com.ourincheon.wazap.WazapService;

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
 * Created by Youngdo on 2016-02-18.
 */
public class ListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    TextView dday, title, cate, man;
    ImageView small;
    Button bt;
    /*
    Contests contest;
    ArrayList<ApplyList> apply_list = new ArrayList<ApplyList>();
    int count;
    ApplyList con;
    */

    public ListAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        //list = new ArrayList<Contests>();
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        String access_token = pref.getString("access_token","");
     //   loadPage(access_token);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.require_ltem, null, true);
        dday = (TextView) rowView.findViewById(R.id.dday);
        title = (TextView) rowView.findViewById(R.id.title);
        cate = (TextView) rowView.findViewById(R.id.cate);
        man = (TextView) rowView.findViewById(R.id.man);
        bt = (Button) rowView.findViewById(R.id.require);

        dday.setText("D-14");
       // title.setText(apply_list.get(position).getTitle());
        cate.setText("영상/ucc/사진");
        man.setText("모집인원 5명");
        return rowView;
    }

    @Override
    public int getCount() {
        return 5;
    }
/*
    void loadPage(String access_token)
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
                    contest = response.body();

                    //user = response.body();
                    Log.d("SUCCESS", contest.getMsg());

                    String result = new Gson().toJson(contest);
                    Log.d("SUCESS-----", result);

                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();

                        for(int i =0; i<count; i++) {
                            con = new ApplyList();
                            con.setTitle(jsonArr.getJSONObject(i).getString("title"));
                            con.setCategories(jsonArr.getJSONObject(i).getString("categories"));
                            con.setPeriod(jsonArr.getJSONObject(i).getString("period"));
                            con.setRecruitment(Integer.parseInt(jsonArr.getJSONObject(i).getString("recruitment")));
                            con.setContests_id(Integer.parseInt(jsonArr.getJSONObject(i).getString("contests_id")));
                            addItem(con);
                            System.out.println(con.getTitle());
                            System.out.println(apply_list.size()+"-------------------");
                        }

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

    public void addItem(ApplyList apply)
    {
        apply_list.add(apply);
        notifyDataSetChanged();
    }*/
}
