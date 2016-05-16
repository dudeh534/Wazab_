package com.ourincheon.wazap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.KaKao.infoKaKao;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.ContestInfo;
import com.ourincheon.wazap.facebook.HttpService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hsue.
 */

public class RecruitActivity extends AppCompatActivity {
    String[] Category_arr ={"광고/아이디어/마케팅","디자인","사진/UCC","게임/소프트웨어","해외","기타"};
    EditText reTitle, reCTitle, reHost, reNum, reIntro,  rePos;
    Button reDate, reBack ,reLoc;
    TextView save;
    final CheckBox[] checkbox = new CheckBox[6];
    ContestData con;
    ContestInfo contest2;
    int mode,contest_id;
    String access_token;
    int year, month, day;
    int count;
    String state,substate;
    ArrayAdapter<String> city,subcity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_new);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");

        contest2 = new ContestInfo();

        reTitle = (EditText) findViewById(R.id.reTitle);
        reCTitle = (EditText) findViewById(R.id.reCTitle);
        reHost = (EditText) findViewById(R.id.reHost);
        reNum = (EditText) findViewById(R.id.reNum);
        reDate = (Button) findViewById(R.id.reDate);
        reLoc = (Button) findViewById(R.id.reLoc);
        rePos = (EditText) findViewById(R.id.rePos);
        reIntro = (EditText) findViewById(R.id.reIntro);
        reBack = (Button) findViewById(R.id.reBack);

        city = new ArrayAdapter<String>(RecruitActivity.this, android.R.layout.select_dialog_singlechoice);
        getList();
        subcity = new ArrayAdapter<String>(RecruitActivity.this, android.R.layout.select_dialog_singlechoice);

        //* 카테고리 선택용 체크박스 *//
        CompoundButton.OnCheckedChangeListener checker = new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b) {
                if(count == 2 && b){
                    cb.setChecked(false);
                    Toast.makeText(getApplicationContext(), "카테고리는 두개까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }else if(b){
                    count++;
                    cb.setTextColor(Color.BLUE);
                    if(cb.getText().equals(Category_arr[0]))
                        cb.setButtonDrawable(R.drawable.detail_icon_marketing);
                    else if(cb.getText().equals(Category_arr[1]))
                        cb.setButtonDrawable(R.drawable.detail_icon_design);
                    else if(cb.getText().equals(Category_arr[2]))
                        cb.setButtonDrawable(R.drawable.detail_icon_video);
                    else if(cb.getText().equals(Category_arr[3]))
                        cb.setButtonDrawable(R.drawable.detail_icon_it);
                    else if(cb.getText().equals(Category_arr[4]))
                        cb.setButtonDrawable(R.drawable.detail_icon_idea);
                    else if(cb.getText().equals(Category_arr[5]))
                        cb.setButtonDrawable(R.drawable.detail_icon_scenario);

                }else if(!b){
                    count--;
                    cb.setTextColor(Color.BLACK);
                    if(cb.getText().equals(Category_arr[0]))
                        cb.setButtonDrawable(R.drawable.detail_disable_marketing);
                    else if(cb.getText().equals(Category_arr[1]))
                        cb.setButtonDrawable(R.drawable.detail_disable_design);
                    else if(cb.getText().equals(Category_arr[2]))
                        cb.setButtonDrawable(R.drawable.detail_disable_video);
                    else if(cb.getText().equals(Category_arr[3]))
                        cb.setButtonDrawable(R.drawable.detail_disable_it);
                    else if(cb.getText().equals(Category_arr[4]))
                        cb.setButtonDrawable(R.drawable.detail_disable_idea);
                    else if(cb.getText().equals(Category_arr[5]))
                        cb.setButtonDrawable(R.drawable.detail_disable_scenario);

                }
            }

        };
        checkbox[0] = (CheckBox) findViewById(R.id.checkAd);
        checkbox[0].setOnCheckedChangeListener(checker);
        checkbox[1] = (CheckBox) findViewById(R.id.checkDe);
        checkbox[1].setOnCheckedChangeListener(checker);
        checkbox[2] = (CheckBox) findViewById(R.id.checkUc);
        checkbox[2].setOnCheckedChangeListener(checker);
        checkbox[3] = (CheckBox) findViewById(R.id.checkIt);
        checkbox[3].setOnCheckedChangeListener(checker);
        checkbox[4] = (CheckBox) findViewById(R.id.checkFo);
        checkbox[4].setOnCheckedChangeListener(checker);
        checkbox[5] = (CheckBox) findViewById(R.id.checkEtc);
        checkbox[5].setOnCheckedChangeListener(checker);



        //** 날짜 선택용 DatePicker **//
        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        reDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RecruitActivity.this, dateSetListener, year, month, day).show();
            }
        });

        //** 지역정보 선택용 **//
        reLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertCity = new AlertDialog.Builder(
                        RecruitActivity.this);
                alertCity.setTitle("항목중에 하나를 선택하세요.");
                alertCity.setAdapter(city, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getCitybased(city.getItem(which));

                        AlertDialog.Builder inCity = new AlertDialog.Builder(RecruitActivity.this);
                        inCity.setTitle("군구를 선택하세요.");
                        inCity.setAdapter(subcity, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                substate = subcity.getItem(which);
                                Toast.makeText(getApplicationContext(), state + " "+ substate, Toast.LENGTH_SHORT).show();
                                reLoc.setText(state+" "+substate);
                            }
                        });
                        inCity.show();
                    }
                });
                alertCity.show();
            }
        });

        reBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {finish();}
        });

        // 마스트상세페이지에서 수정하기로 온 경우, 화면 그려주기
        Intent intent = getIntent();
        mode = intent.getExtras().getInt("edit");
        con = (ContestData) intent.getExtras().getSerializable("contestD");
        if (mode == 1) {
            reTitle.setText(con.getTitle());
            reCTitle.setText(con.getCont_title());
            System.out.println(con.getTitle());
            reHost.setText(con.getHosts());
            reNum.setText(String.valueOf(con.getRecruitment()));
            reDate.setText(con.getPeriod());
            reIntro.setText(con.getCover());
            rePos.setText(con.getPositions());
            reLoc.setText(con.getCont_locate());
            contest_id = con.getContests_id();
        }

        // 저장 버튼 눌렀을 시
        save = (TextView) findViewById(R.id.reButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dday date = new Dday();
                if(date.dday(reDate.getText().toString())<0)
                    Toast.makeText(RecruitActivity.this, "이미 지난 기간을 선택할 수 없습니다.", Toast.LENGTH_SHORT).show();
                else if(reTitle.getText().toString().equals("") || reCTitle.getText().toString().equals("") || reNum.getText().toString().equals("")
                    ||reHost.getText().toString().equals("") || reIntro.getText().toString().equals("") || reDate.getText().toString().equals("")
                        || reLoc.getText().toString().equals("") || rePos.getText().toString().equals("")
                        || (checkbox[0].isChecked() == false && checkbox[1].isChecked() == false && checkbox[2].isChecked() == false
                            && checkbox[3].isChecked() == false && checkbox[4].isChecked() == false &&checkbox[5].isChecked() == false  ) )
                    Toast.makeText(getApplicationContext(), "필수사항을 모두 입력해주세요.", Toast.LENGTH_LONG).show();
                else
                {
                    contest2.setTitle(reTitle.getText().toString());
                    contest2.setCont_title(reCTitle.getText().toString());
                    contest2.setRecruitment(Integer.parseInt(reNum.getText().toString()));
                    contest2.setHosts(reHost.getText().toString());
                    contest2.setCover(reIntro.getText().toString());
                    for(int i=0; i<6; i++) {
                        if (checkbox[i].isChecked()) {
                            contest2.setCategories(checkbox[i].getText().toString());
                        }
                    }
                    contest2.setPeriod(reDate.getText().toString());
                    contest2.setCont_locate(reLoc.getText().toString());
                    contest2.setPositions(rePos.getText().toString());

                    if (mode == 0)
                        sendContest(contest2);
                    else
                        editCon(contest2);

                    ChangeStatus status = ChangeStatus.getInstance();
                    status.setNewed();
                    finish();
                }
            }
        });
    }

    // 날짜 선택 처리
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
            Dday date = new Dday();
            reDate.setText(msg);
            if(date.dday(reDate.getText().toString())<0) {
                Toast.makeText(RecruitActivity.this, "이미 지난 기간을 선택할 수 없습니다.", Toast.LENGTH_SHORT).show();
                reDate.setText(null);
            }
        }

    };


    //** 서버에서 시구 목록 받아오기 **//
    void getList()
    {
        String baseUrl = "http://come.n.get.us.to/";
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WazapService service = client.create(WazapService.class);

        Call<LinkedTreeMap> call = service.getLocatelist(access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {
                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();
                    String data = temp.get("data").toString();

                    String[] split = (data.substring(1,data.length()-1)).split(", ");

                    for(int i=0; i<split.length; i++) {
                        System.out.println(split[i]);
                        city.add(split[i]);
                    }

                    System.out.println(data);
                    if (result) {
                        Log.d("리스트 받아오기: ", msg);
                    } else {
                        Log.d("리스트 받기 실패: ", msg);
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body is NULL", response.message());
                }
                else {
                    Log.d("Response Error Body", response.errorBody().toString());
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    //** 시구 목록 받아오기 **//
    void getCitybased(final String cityname)
    {
        String baseUrl = "http://come.n.get.us.to/";
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WazapService service = client.create(WazapService.class);

        Call<LinkedTreeMap> call = service.getLocatebasedlist(access_token, cityname);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {
                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();
                    String data = temp.get("data").toString();

                    String[] split = (data.substring(1,data.length()-1)).split(", ");

                    state = cityname;
                    for(int i=0; i<split.length; i++) {
                        subcity.add(split[i]);
                    }

                    System.out.println(data);
                    if (result) {
                        Log.d("군구리스트 받아오기: ", msg);
                    } else {
                        Log.d("군구리스트 받기 실패: ", msg);
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body is NULL", response.message());
                }
                else {
                    Log.d("Response Error Body", response.errorBody().toString());
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    //** 서버에 수정요청 보내기 **//
    void editCon(ContestInfo contest)
    {
        String baseUrl = "http://come.n.get.us.to/";
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WazapService service = client.create(WazapService.class);

        Call<LinkedTreeMap> call = service.editContest(access_token,String.valueOf(contest_id), contest);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {
                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("수정 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                        ((MasterJoinActivity)(MasterJoinActivity.mContext)).onResume();

                    } else {
                        Log.d("수정 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "수정이 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body is NULL", response.message());
                    Toast.makeText(getApplicationContext(), "수정이 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("Response Error Body", response.errorBody().toString());
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    //** 서버에 모집글 저장요청보내기 **//
    void sendContest(ContestInfo contest)
    {
        String baseUrl = "http://come.n.get.us.to/";
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WazapService service = client.create(WazapService.class);

        Call<LinkedTreeMap> call = service.createContests(access_token, contest);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {
                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);

                        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "저장이 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body is NULL", response.message());
                    Toast.makeText(getApplicationContext(), "저장이 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("Response Error Body", response.errorBody().toString());
                    System.out.println(response.code());
                    Toast.makeText(getApplicationContext(), "저장이 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error", "errormsg"+t.getMessage());
            }
        });
    }

}