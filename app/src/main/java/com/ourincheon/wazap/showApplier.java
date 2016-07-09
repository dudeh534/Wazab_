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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.Retrofit.regUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hsue.
 */
public class showApplier extends AppCompatActivity {

    public static Context showContext;
    ImageView profileImg;
    String thumbnail;
    regUser reguser;
    NotoTextView Title;
    private TextView sName, sMajor, sUniv, sLoc, sKakao, sIntro, sExp,sSkill,pBtn;
    ImageView sImg;
    String user_id,contest_id,applies_id;
    int is_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        showContext = this;

        Intent intent = getIntent();
        System.out.println(intent.getExtras().getString("applies_id"));
        System.out.println(intent.getExtras().getString("contest_id"));
        is_ok = intent.getExtras().getInt("is_ok");
        user_id =intent.getExtras().getString("user_id");
        contest_id =intent.getExtras().getString("contest_id");
        applies_id =intent.getExtras().getString("applies_id");

        Button aBefore = (Button) findViewById(R.id.aBefore);
        ImageButton settingBtn = (ImageButton) findViewById(R.id.setting_btn);
        aBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Title =(NotoTextView)findViewById(R.id.mytext);
        sName = (TextView) findViewById(R.id.sName);
        sMajor = (TextView)  findViewById(R.id.sMajor);
        sUniv = (TextView)  findViewById(R.id.sUniv);
        sLoc = (TextView)  findViewById(R.id.sLoc);
        sKakao = (TextView)  findViewById(R.id.sKakao);
        sIntro = (TextView) findViewById(R.id.sIntro);
        sExp = (TextView) findViewById(R.id.sExp);
        sSkill = (TextView) findViewById(R.id.sSkill);
        profileImg = (ImageView) findViewById(R.id.sPro);
        sImg = (ImageView)findViewById(R.id.fab);
        pBtn = (TextView)findViewById(R.id.pButton);

        Title.setText("신청자 프로필");


        loadPage();

        // 수락여부에 따라 다른 텍스트보여주기, 서버로 요청하기
        pBtn = (TextView) findViewById(R.id.pButton);
        if(is_ok == 0)
            pBtn.setText("수락하기");
        else
            pBtn.setText("수락취소");

        pBtn.setVisibility(View.VISIBLE);
        pBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_ok == 0)
                    is_ok = 1;
                else
                    is_ok = 0;
                changeMem();
            }
        });

    }

    //*** 서버로 멤버 변경에 대해 요청하기 ***//
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
                        loadPage();
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

    //*** 서버에서 신청자 정보 받아오기 ***//
    void loadPage()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Log.d("SUCCESS", user_id );

        Call<regUser> call = service.getUserInfo(user_id);
        call.enqueue(new Callback<regUser>() {
            @Override
            public void onResponse(Response<regUser> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    reguser = response.body();

                    String result = new Gson().toJson(reguser);
                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        Log.d("username", jsonArr.getJSONObject(0).getString("username"));
                        sName.setText(jsonArr.getJSONObject(0).getString("username"));
                        sMajor.setText(jsonArr.getJSONObject(0).getString("major"));
                        sUniv.setText(jsonArr.getJSONObject(0).getString("school"));
                        sLoc.setText(jsonArr.getJSONObject(0).getString("locate"));
                        sIntro.setText(jsonArr.getJSONObject(0).getString("introduce"));
                        sExp.setText(jsonArr.getJSONObject(0).getString("exp"));
                        sSkill.setText(jsonArr.getJSONObject(0).getString("skill"));

                        // 카카오아이디 수락된 경우만 보이
                        if(is_ok == 0)
                            sKakao.setText("수락되어야 볼 수 있습니다.");
                        else {
                            System.out.println(is_ok);
                            sKakao.setText(jsonArr.getJSONObject(0).getString("kakao_id"));
                        }

                        if(is_ok == 0)
                            pBtn.setText("수락하기");
                        else
                            pBtn.setText("수락취소");

                        try {
                            thumbnail = URLDecoder.decode(jsonArr.getJSONObject(0).getString("profile_img"), "EUC_KR");
                            Glide.with(showContext).load(thumbnail).asBitmap().centerCrop().override(150, 150).into(new BitmapImageViewTarget(sImg) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(showContext.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    sImg.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println(thumbnail);

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
                Log.e("Errorglg''';kl", t.getMessage());
            }
        });
    }

    // 뒤로가기 버튼 터치시 -> 변경사항 ApplierList에 반영
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //((ContestList)(ContestList.mContext)).onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

       /* if(flag==0) {
            getMenuInflater().inflate(R.menu.menu_show_mypage, menu);
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


       /* if(flag==0) {
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_edit) {
                Intent i = new Intent(showApplier.this, MypageActivity.class);
                startActivity(i);
            }
        }
*/
        return super.onOptionsItemSelected(item);
    }
}
