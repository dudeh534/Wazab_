package com.ourincheon.wazap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.ourincheon.wazap.Retrofit.UserInfo;
import com.ourincheon.wazap.Retrofit.regMsg;
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

public class MypageActivity extends AppCompatActivity {

    Context mcontext;
    ImageView profileImg;
    String thumbnail;
    regUser reguser;
    TextView eName;
    private EditText eMajor, eUniv, eLoc, eKakao, eIntro, eExp,eSkill;
    String access_token, kakao_id, username, password, school, major, locate, introduce, exp,skill;
    int age;
    UserInfo userInfo;
    regMsg res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Button aBefore = (Button) findViewById(R.id.aBefore);
        NotoButtonView editBtn = (NotoButtonView) findViewById(R.id.edit_btn);
        aBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 우측상단 버튼 누를시, 저장되어 원래 페이지로 이동
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = eName.getText().toString().trim();
                major = eMajor.getText().toString().trim();
                school = eUniv.getText().toString().trim();
                locate = eLoc.getText().toString().trim();
                kakao_id = eKakao.getText().toString();
                introduce = eIntro.getText().toString().trim();
                exp = eExp.getText().toString().trim();
                skill = eSkill.getText().toString().trim();

                userInfo = new UserInfo( kakao_id, username, school, 94, major , skill, locate, introduce, exp);

                if(major.equals("") || locate.equals("") || kakao_id.equals("") || skill.equals("") || introduce.equals("") || exp.equals(""))
                    Toast.makeText(getApplicationContext(), "필수사항을 모두 입력해주세요.", Toast.LENGTH_LONG).show();
                else
                {
                    postInfo(userInfo);
                    ((showMypageActivity)(showMypageActivity.showContext)).onResume();
                    finish();
                }
            }
        });

        mcontext = this;
        eName = (TextView) findViewById(R.id.eName);
        eMajor = (EditText) findViewById(R.id.eMajor);
        eUniv = (EditText) findViewById(R.id.eUniv);
        eLoc = (EditText) findViewById(R.id.eLoc);
        eKakao = (EditText) findViewById(R.id.eKakao);
        eIntro = (EditText) findViewById(R.id.eIntro);
        eExp = (EditText) findViewById(R.id.eExp);
        eSkill = (EditText) findViewById(R.id.eSkill);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");
        profileImg = (ImageView)findViewById(R.id.ePro);

        getInfo();
    }

    //*** 서버에서 프로필 정보 요청 ***//
    void getInfo()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String user_id = pref.getString("user_id", "");

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
                        eName.setText(jsonArr.getJSONObject(0).getString("username"));
                        eMajor.setText(jsonArr.getJSONObject(0).getString("major"));
                        eUniv.setText(jsonArr.getJSONObject(0).getString("school"));
                        eLoc.setText(jsonArr.getJSONObject(0).getString("locate"));
                        eKakao.setText(jsonArr.getJSONObject(0).getString("kakao_id"));
                        eIntro.setText(jsonArr.getJSONObject(0).getString("introduce"));
                        eExp.setText(jsonArr.getJSONObject(0).getString("exp"));
                        eSkill.setText(jsonArr.getJSONObject(0).getString("skill"));

                        try {
                            thumbnail = URLDecoder.decode(jsonArr.getJSONObject(0).getString("profile_img"), "EUC_KR");
                            Glide.with(mcontext).load(thumbnail).asBitmap().centerCrop().error(R.drawable.icon_user).into(new BitmapImageViewTarget(profileImg) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(mcontext.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    profileImg.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
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
                Log.e("Errorglg''';kl", t.getMessage());
            }
        });
    }

    //*** 서버에 정보 저장하기 ***//
    void postInfo(UserInfo userInfo)
    {
         Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<regMsg> call = service.createInfo(access_token, userInfo);
        call.enqueue(new Callback<regMsg>() {
            @Override
            public void onResponse( Response<regMsg> response) {
                if (response.isSuccess() && response.body() != null) {
                    res = response.body();
                    Log.d("SUCCESS", res.getMsg());
                    Toast.makeText(getApplicationContext(), "수정 되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                    Toast.makeText(getApplicationContext(), "수정 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                    Toast.makeText(getApplicationContext(), "수정 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure( Throwable t) {
                t.printStackTrace();
                Log.e("Error", t.getMessage());
                Toast.makeText(getApplicationContext(), "수정 실패했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
