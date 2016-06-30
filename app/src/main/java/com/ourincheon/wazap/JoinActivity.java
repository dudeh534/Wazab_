package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.Retrofit.reqContest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;


public class JoinActivity extends AppCompatActivity {
    Context context;
    reqContest contest;
    TextView jTitle,jCTitle,jButton,jApply,jRec,jName,jCover,jMem,jDate,jHost,jLoc,jPos,jPro,jKakao;
    TextView jCate[] = new TextView[2];
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
        jCate[0] =  (TextView) findViewById(R.id.jCate1);
        jCate[1] =  (TextView) findViewById(R.id.jCate2);
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

        // 신청 값에 따라 서버에 신청 or 취소 요청
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

        // 서버에 찜요청
        jPick = (Button) findViewById(R.id.jPick);
        jPick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pickContest(num, access_token);
            }
        });

        // 작성자 프로필보기
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

    //*** 서버에 신청취소 요청보내기 ***//
    void deleteApply(String contest)
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
        loadPage(num);
    }

    //*** 서버에 모집글 찜요청 보내기 ***//
    void pickContest(String num, final String access_token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

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
                        removeClip();       // 이미 찜한 게시물일 경우, 찜 취소
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

    //*** 서버에 찜취소 요청보내기 ***//
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

    //*** 서버에 모집글 신청요청 보기 ***//
    void applyContest(String num, String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

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

    //*** 서버에서 상세 모집글 정보 가져오기 ***//
    void loadPage(final String num)
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

                    if(contest.getMsg().equals("모집글 정보가 없습니다.")) {
                        Toast.makeText(getApplicationContext(), contest.getMsg(), Toast.LENGTH_LONG).show();
                        finish();
                    } else if (contest.getData() == null) {
                        loadPage(num);
                    } else {
                        jTitle.setText(contest.getData().getTitle());
                        jCTitle.setText(contest.getData().getCont_title());
                        //jCate.setText(contest.getData().getCategories());
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

                        // 카테고리 목록별로 아이콘으로 나타냄
                        String[] category = contest.getData().getCategories().split(",");
                        if (category.length == 1) {
                            jCate[0].setText(category[0].trim());
                            jCate[1].setVisibility(View.GONE);
                            /*
                            // 카테고리에 따라 그림으로
                            switch (category[0].trim()) {
                                case "사진/UCC":
                                    jCate[0].setImageResource(R.drawable.detail_icon_video);
                                    break;
                                case "디자인":
                                    jCate[0].setImageResource(R.drawable.detail_icon_design);
                                    break;
                                case "게임/소프트웨어":
                                    jCate[0].setImageResource(R.drawable.detail_icon_it);
                                    break;
                                case "해외":
                                    jCate[0].setImageResource(R.drawable.detail_icon_idea);
                                    break;
                                case "광고/아이디어/마케팅":
                                    jCate[0].setImageResource(R.drawable.detail_icon_marketing);
                                    break;
                                case "기타":
                                    jCate[0].setImageResource(R.drawable.detail_icon_scenario);
                                    break;

                            }*/
                        } else {
                            for (int i=0; i<category.length; i++) {
                                jCate[i].setText(category[i].trim());
                                /*
                                // 카테고리에 따라 그림으로
                                switch (category[i].trim()) {
                                    case "사진/UCC":
                                        jCate[i].setImageResource(R.drawable.detail_icon_video);
                                        break;
                                    case "디자인":
                                        jCate[i].setImageResource(R.drawable.detail_icon_design);
                                        break;
                                    case "게임/소프트웨어":
                                        jCate[i].setImageResource(R.drawable.detail_icon_it);
                                        break;
                                    case "해외":
                                        jCate[i].setImageResource(R.drawable.detail_icon_idea);
                                        break;
                                    case "광고/아이디어/마케팅":
                                        jCate[i].setImageResource(R.drawable.detail_icon_marketing);
                                        break;
                                    case "기타":
                                        jCate[i].setImageResource(R.drawable.detail_icon_scenario);
                                        break;

                                }*/
                            }
                        }

                        // 찜여부에 따라 이미지 다르게
                        if (contest.getData().getIs_clip() == 0)
                            jPick.setBackgroundResource(R.drawable.heart1);
                        else
                            jPick.setBackgroundResource(R.drawable.heart2);

                        // 디데이
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

                        //* 멤버리스트 이미지로 붙이기 *//
                        System.out.println("membersize---------------" + contest.getData().getMembersize());
                        imgLayout.removeAllViews();
                        for (int i = 0; i < contest.getData().getMembersize(); i++) {
                            final int idx = i;
                            final ImageView img = new ImageView(context);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 0);
                            img.setLayoutParams(params);
                            System.out.println("fffffffdfdsadfsd"+contest.getData().getMemberList(i).getProfile_img());
                            //Glide.with(context).load(contest.getData().getMemberList(i).getProfile_img()).error(R.drawable.icon_user).override(70,70).crossFade().into(img);
                            int size = PixelToDp(context, 300);
                            Glide.with(context).load(contest.getData().getMemberList(i).getProfile_img()).asBitmap().centerCrop().override(size, size).into(new BitmapImageViewTarget(img) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    img.setImageDrawable(circularBitmapDrawable);

                                }
                            });
                            // 이미지로 붙인 멤버 클릭시, 팀원정보 상세보기로 이동
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
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
