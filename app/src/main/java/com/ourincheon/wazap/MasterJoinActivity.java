package com.ourincheon.wazap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
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
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.reqContest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MasterJoinActivity extends AppCompatActivity {

    public static Context mContext;
    reqContest contest;
    ContestData contestData;
    ContestData editconData;
    TextView jTitle,jCTitle,jButton,jmList,jApply,jRec,jName,jCover,jMem,jDate,jHost,jLoc,jPos,jKakao;
    TextView jCate[] = new TextView[2];
    Button eBtn,jBefore;
    ImageView jImg;
    String access_token,num;
    AlertDialog.Builder ad,deleteD;
    CharSequence list[] = {"수정하기", "삭제하기","취소"};
    LinearLayout imgLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_join);

        mContext = this;

        contestData = new ContestData();
        editconData = new ContestData();

        jTitle = (TextView) findViewById(R.id.jmTitle);
        jCTitle = (TextView) findViewById(R.id.jCTitle);
        jCate[0] =  (TextView) findViewById(R.id.jCate1);
        jCate[1] =  (TextView) findViewById(R.id.jCate2);
        jApply = (TextView) findViewById(R.id.jApply);
        jRec = (TextView) findViewById(R.id.jRec);
        jName = (TextView) findViewById(R.id.jName);
        jCover = (TextView) findViewById(R.id.jCover);
        jMem = (TextView) findViewById(R.id.jMem);
        jDate = (TextView) findViewById(R.id.jmDate);
        jHost = (TextView) findViewById(R.id.jHost);
        jLoc = (TextView) findViewById(R.id.jLoc);
        jPos = (TextView) findViewById(R.id.jPos);
        jKakao = (TextView) findViewById(R.id.jKakao);

        jImg = (ImageView) findViewById(R.id.jImg);

        imgLayout= (LinearLayout) findViewById(R.id.imgLayout);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");

        Intent intent = getIntent();
        num =  intent.getExtras().getString("id");
        loadPage(num);

        //* 모집글 마감 요청 확인 메세지 *//
        deleteD = new AlertDialog.Builder(this);
        deleteD.setMessage("모집글을 마감하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endContest();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // 모집마감 버튼 누를시, 확인메세지 출력
        jButton = (TextView) findViewById(R.id.jmButton);
        jButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = deleteD.create();
                alert.show();
            }
        });

        jBefore = (Button) findViewById(R.id.jmBefore);
        jBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 우측 상단 ...메뉴 터치시
        ad = new AlertDialog.Builder(this);
        ad.setTitle("팀원모집");
        ad.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)         //  모집글 수정
                    editCont();
                if (which == 1) {       // 모집글 삭제
                    ChangeStatus status = ChangeStatus.getInstance();
                    status.setDeleted();
                    delCont();
                }
                if (which == 2)         // 취소
                    dialog.cancel();
            }
        });

        // 수정,삭제버튼
        eBtn = (Button) findViewById(R.id.jmEdit);
        eBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
            }
        });

        //신청자리스트보기 버튼
        jmList = (TextView) findViewById(R.id.jmList);
        jmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MasterJoinActivity.this, ApplierList.class);
                intent.putExtra("id", num);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPage(num);
    }

    //*** 모집글 수정페이지로 이동 ***//
    void editCont()
    {
        Intent intent = new Intent(MasterJoinActivity.this, RecruitActivity.class);
        intent.putExtra("edit",1);
        intent.putExtra("contestD", editconData);
        startActivity(intent);
    }

    //*** 서버에 모집글 삭제요청 보내기 ***//
    void delCont()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<LinkedTreeMap> call = service.delContest(num,access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("삭제 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Log.d("삭제 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "삭제 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    //*** 서버에 모집글 마감요청 보내기 ***//
    void endContest( )
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<LinkedTreeMap> call = service.finishContest(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "마감되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "마감 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    //*** 마스트 상세페이지 정보 불러오기 ***//
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

                    if (contest.getData() == null) {
                        loadPage(num);
                    } else {
                        editconData.setContests_id(contest.getData().getContests_id());
                        jTitle.setText(contest.getData().getTitle());
                        editconData.setTitle(contest.getData().getTitle());
                        jCTitle.setText(contest.getData().getCont_title());
                        editconData.setCont_title(contest.getData().getCont_title());
                        jApply.setText(String.valueOf(contest.getData().getAppliers()));
                        jMem.setText(String.valueOf(contest.getData().getMembers()));
                        jRec.setText(" / " + String.valueOf(contest.getData().getRecruitment()));
                        editconData.setRecruitment(contest.getData().getRecruitment());
                        jName.setText(contest.getData().getUsername());
                        editconData.setUsername(contest.getData().getUsername());
                        jCover.setText(contest.getData().getCover());
                        editconData.setCover(contest.getData().getCover());
                        jHost.setText(contest.getData().getHosts());
                        editconData.setHosts(contest.getData().getHosts());
                        jLoc.setText(contest.getData().getCont_locate());
                        editconData.setCont_locate(contest.getData().getCont_locate());
                        jPos.setText(contest.getData().getPositions());
                        editconData.setPositions(contest.getData().getPositions());
                        jKakao.setText(contest.getData().getKakao_id());


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
                            for (int i = 0; i < category.length; i++) {
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

                        try {
                            String thumb = URLDecoder.decode(contest.getData().getProfile_img(), "EUC_KR");
                            //Glide.with(mContext).load(thumb).error(R.drawable.icon_user).override(50,50).crossFade().into(jImg);
                            Glide.with(mContext).load(thumb).asBitmap().centerCrop().error(R.drawable.icon_user).into(new BitmapImageViewTarget(jImg) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    jImg.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        // 멤버리스트 이미지로 붙이기
                        System.out.println("membersize---------------" + contest.getData().getMembersize());
                        imgLayout.removeAllViews();
                        for (int i = 0; i < contest.getData().getMembersize(); i++) {
                            final int idx = i;
                            final ImageView img = new ImageView(mContext);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 20, 0);
                            img.setLayoutParams(params);
                            System.out.println(contest.getData().getMemberList(i).getProfile_img());
                            //Glide.with(context).load(contest.getData().getMemberList(i).getProfile_img()).error(R.drawable.icon_user).override(70,70).crossFade().into(img);
                            int size = PixelToDp(mContext, 150);
                            Glide.with(mContext).load(contest.getData().getMemberList(i).getProfile_img()).asBitmap().override(size, size).centerCrop().error(R.drawable.icon_user).into(new BitmapImageViewTarget(img) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    img.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                            // 이미지로 붙인 멤버 클릭시, 팀원정보 상세보기로 이동
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    System.out.println("---------------------" + contest.getData().getMemberList(idx).getUsers_id());
                                    Intent intent = new Intent(MasterJoinActivity.this, showMypageActivity.class);
                                    intent.putExtra("user_id", contest.getData().getMemberList(idx).getUsers_id());
                                    intent.putExtra("flag", 2);
                                    startActivity(intent);
                                }
                            });
                            imgLayout.addView(img);
                        }

                        String[] parts = contest.getData().getPeriod().split("T");
                        Dday day = new Dday();
                        jDate.setText("D - " + day.dday(parts[0]));
                        editconData.setPeriod(parts[0]);

                        contestData = contest.getData();
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

