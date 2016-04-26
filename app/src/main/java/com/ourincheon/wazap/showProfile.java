package com.ourincheon.wazap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.Retrofit.regUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class showProfile extends AppCompatActivity {
    ImageView profileImg;
    String thumbnail;
    regUser reguser;
    private TextView sName, sMajor, sUniv, sLoc, sKakao, sIntro, sExp;
    int flag;
    TextView pButton;
    String user_id,contest_id,applies_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        Intent intent = getIntent();

        System.out.println(intent.getExtras().getString("applies_id"));
        System.out.println(intent.getExtras().getString("contest_id"));
        flag = intent.getExtras().getInt("flag");
        user_id =intent.getExtras().getString("user_id");
        contest_id =intent.getExtras().getString("contest_id");
        applies_id =intent.getExtras().getString("applies_id");

        sName = (TextView) findViewById(R.id.pName);
        sMajor = (TextView)  findViewById(R.id.pMajor);
        sUniv = (TextView)  findViewById(R.id.pUniv);
        sLoc = (TextView)  findViewById(R.id.pLoc);
        sKakao = (TextView)  findViewById(R.id.pKakao);
        sIntro = (TextView) findViewById(R.id.pIntro);
        sExp = (TextView) findViewById(R.id.pExp);


        profileImg = (ImageView)findViewById(R.id.pPro);
        thumbnail = intent.getExtras().getString("thumbnail");


        Glide.with(this).load(thumbnail).error(R.drawable.icon_user).override(150,150).crossFade().into(profileImg);

        loadPage(user_id);

        pButton = (TextView) findViewById(R.id.pButton);
        if(flag == 0)
            pButton.setText("수락하기");
        else
            pButton.setText("수락취소");

        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==0)
                    flag = 1;
                else
                    flag = 0;

                changeMem();
            }
        });
    }

    void changeMem()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String access_token = pref.getString("access_token", "");

        Call<LinkedTreeMap> call = service.changeMember(contest_id, applies_id, access_token);
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
                            loadPage(user_id);
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

    void loadPage(String user_id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<regUser> call = service.getUserInfo(user_id);
        call.enqueue(new Callback<regUser>() {
            @Override
            public void onResponse( Response<regUser> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    reguser = response.body();

                    //user = response.body();
                    //Log.d("SUCCESS", reguser.getMsg());

                    String result = new Gson().toJson(reguser);
                    Log.d("SUCESS-----",result);

                    JSONObject jsonRes;
                    try{
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        Log.d("username", jsonArr.getJSONObject(0).getString("username"));
                        sName.setText(jsonArr.getJSONObject(0).getString("username"));
                        sMajor.setText(jsonArr.getJSONObject(0).getString("major"));
                        sUniv.setText(jsonArr.getJSONObject(0).getString("school"));
                        sLoc.setText(jsonArr.getJSONObject(0).getString("locate"));
                        if(flag == 0)
                            sKakao.setVisibility(View.INVISIBLE);
                        else {
                            sKakao.setVisibility(View.VISIBLE);
                            System.out.println(flag);
                            sKakao.setText(jsonArr.getJSONObject(0).getString("kakao_id"));
                        }
                        sIntro.setText(jsonArr.getJSONObject(0).getString("introduce"));
                        sExp.setText(jsonArr.getJSONObject(0).getString("exp"));

                        if(flag == 0)
                            pButton.setText("수락하기");
                        else
                            pButton.setText("수락취소");

                    }catch (JSONException e)
                    {};

                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure( Throwable t) {
                t.printStackTrace();
                Log.e("Errorglg''';kl", t.getMessage());
            }
        });
    }


    // 뒤로가기 버튼 터치시 -> 변경사항 ApplierList에 반영
}

