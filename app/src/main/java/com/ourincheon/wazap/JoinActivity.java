package com.ourincheon.wazap;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.MemberList;
import com.ourincheon.wazap.Retrofit.reqContest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;


public class JoinActivity extends AppCompatActivity {
    Context context;
    reqContest contest;
    TextView jTitle,jCTitle,jButton,jCate,jApply,jRec,jName,jCover,jMem,jDate,jHost,jLoc,jPos,jPro,jKakao;
    ImageView jImg;
    String access_token,num,Writer;
    int is_apply;
    Button jPick,jBefore;
    LinearLayout imgLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        context = this;

        jTitle = (TextView) findViewById(R.id.jTitle);
        jCTitle = (TextView) findViewById(R.id.jCTitle);
        jCate =  (TextView) findViewById(R.id.jCate);
        jApply = (TextView) findViewById(R.id.jApply);
        jRec = (TextView) findViewById(R.id.jRec);
        jName = (TextView) findViewById(R.id.jName);
        jCover = (TextView) findViewById(R.id.jCover);
        jMem = (TextView) findViewById(R.id.jMem);
        jDate = (TextView) findViewById(R.id.jDate);
        jHost = (TextView) findViewById(R.id.jHost);
        jLoc = (TextView) findViewById(R.id.jLoc);
        jPos = (TextView) findViewById(R.id.jPos);
        jKakao = (TextView) findViewById(R.id.jKakao);

        jImg = (ImageView) findViewById(R.id.jImg);

        imgLayout= (LinearLayout) findViewById(R.id.imgLayout);

        Intent intent = getIntent();
        num =  intent.getExtras().getString("id");

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");


        jBefore = (Button) findViewById(R.id.jBefore);
        jBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        jButton = (TextView) findViewById(R.id.jButton);

        jButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_apply==0)
                    applyContest(num, access_token);
                else
                    deleteApply(num);
            }
        });

        jPick = (Button) findViewById(R.id.jPick);
        jPick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pickContest(num, access_token);
            }
        });

        jPro = (TextView) findViewById(R.id.jPro);
        jPro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinActivity.this, showMypageActivity.class);
                intent.putExtra("user_id", Writer);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
    }

    void deleteApply(String contest)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("-------------------"+access_token+"---------"+contest);

        Call<LinkedTreeMap> call = service.delApply(contest, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("신청 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "신청 취소되었습니다.", Toast.LENGTH_LONG).show();
                        onResume();
                    } else {
                        Log.d("신청 실패: ", msg);
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
        System.out.println("-----------------------------------------------------------------");
        loadPage(num);
    }

    void pickContest(String num, final String access_token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("-------------------" + access_token);
        Call<LinkedTreeMap> call = service.clipContests(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (!msg.equals("이미 찜한 게시물 입니다.")) {
                        if (result) {
                            Log.d("저장 결과: ", msg);
                            Toast.makeText(getApplicationContext(), "찜 되었습니다.", Toast.LENGTH_SHORT).show();
                            onResume();
                        } else {
                            Log.d("저장 실패: ", msg);
                            Toast.makeText(getApplicationContext(), "찜 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        removeClip();
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

    void removeClip()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<LinkedTreeMap> call = service.delClip(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "찜 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        onResume();
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "찜 취소안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    void applyContest(String num, String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("-------------------"+access_token);
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
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "신청 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    void loadPage(String num)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<reqContest> call = service.getConInfo(num, access_token);
        call.enqueue(new Callback<reqContest>() {
            @Override
            public void onResponse(Response<reqContest> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    contest = response.body();

                    Log.d("SUCCESS", contest.getMsg());

                    if(contest.getMsg().equals("모집글 정보가 없습니다.")) {
                        //System.out.println("======================================");
                        Toast.makeText(getApplicationContext(), contest.getMsg(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        jTitle.setText(contest.getData().getTitle());
                        jCTitle.setText(contest.getData().getCont_title());
                        jCate.setText(contest.getData().getCategories());
                        jApply.setText(String.valueOf(contest.getData().getAppliers()));
                        jMem.setText(String.valueOf(contest.getData().getMembers()));
                        jRec.setText(" / " + String.valueOf(contest.getData().getRecruitment()));
                        jName.setText(contest.getData().getUsername());
                        jCover.setText(contest.getData().getCover());
                        jHost.setText(contest.getData().getHosts());
                        jLoc.setText(contest.getData().getCont_locate());
                        jPos.setText(contest.getData().getPositions());
                        jKakao.setText(contest.getData().getKakao_id());

                        Writer = contest.getData().getCont_writer();

                        if (contest.getData().getIs_clip() == 0)
                            jPick.setBackgroundResource(R.drawable.heart1);
                        else
                            jPick.setBackgroundResource(R.drawable.heart2);


                        String[] parts = contest.getData().getPeriod().split("T");
                        Dday day = new Dday();
                        jDate.setText("D - " + day.dday(parts[0]));

                        try {
                            String thumb = URLDecoder.decode(contest.getData().getProfile_img(), "EUC_KR");
                            //    Glide.with(context).load(thumb).error(R.drawable.icon_user).override(50,50).crossFade().into(jImg);
                            Glide.with(context).load(thumb).asBitmap().centerCrop().error(R.drawable.icon_user).into(new BitmapImageViewTarget(jImg) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    jImg.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        is_apply = contest.getData().getIs_apply();

                        // 신청하기 버튼 글자
                        if (is_apply == 0)
                            jButton.setText("신청하기");
                        else
                            jButton.setText("신청취소하기");

                        // 멤버리스트 이미지로 붙이기
                        System.out.println("membersize---------------" + contest.getData().getMembersize());
                        imgLayout.removeAllViews();
                        for (int i = 0; i < contest.getData().getMembersize(); i++) {
                            final int idx = i;
                            final ImageView img = new ImageView(context);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 20, 0);
                            img.setLayoutParams(params);
                            System.out.println(contest.getData().getMemberList(i).getProfile_img());
                            //Glide.with(context).load(contest.getData().getMemberList(i).getProfile_img()).error(R.drawable.icon_user).override(70,70).crossFade().into(img);
                            int size = PixelToDp(context, 150);
                            Glide.with(context).load(contest.getData().getMemberList(i).getProfile_img()).asBitmap().override(size, size).centerCrop().error(R.drawable.icon_user).into(new BitmapImageViewTarget(img) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    img.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println("---------------------" + contest.getData().getMemberList(idx).getUsers_id());

                                    Intent intent = new Intent(JoinActivity.this, showMypageActivity.class);
                                    intent.putExtra("user_id", contest.getData().getMemberList(idx).getUsers_id());
                                    intent.putExtra("flag", 2);
                                    startActivity(intent);
                                }
                            });
                            imgLayout.addView(img);
                        }
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.e("Error", t.getMessage());
            }
        });
    }

    // pixel값을 dp값으로 변경
    public static int PixelToDp(Context context, int pixel) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = pixel / (metrics.densityDpi / 160f);
        return (int) dp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
