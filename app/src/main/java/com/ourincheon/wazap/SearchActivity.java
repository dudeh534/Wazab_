package com.ourincheon.wazap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ourincheon.wazap.Retrofit.Contests;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    RecyclerView content;
    Context context;
    EditText sBox;
    Button sBtn;
    NotoTextView sText;
    RecyclerAdapter rec;
    List<Recycler_item> items;
    Recycler_item[] item;
    Contests contest;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        content = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        content.setHasFixedSize(true);
        content.setLayoutManager(layoutManager);

        context = this;


        sText = (NotoTextView) findViewById(R.id.searchNo);
        sBox = (EditText) findViewById(R.id.search_box);
        // 입력완료 리슨
        //sBox.setOnEditorActionListener(this);
        sBox.setImeOptions(EditorInfo.IME_ACTION_DONE); // 키보드 확인 버튼 클릭시
        sBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    sBtn.performClick(); // sBtn 클릭
                return false;
            }
        });

        // 검색버튼 터치시 검색
        sBtn = (Button) findViewById(R.id.search_btn);
        sBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items = new ArrayList<>();
                searchTitle(sBox.getText().toString());
            }
        });
    }

    //*** 서버로 검색요청 ***//
    void searchTitle(String text) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String access_token = pref.getString("access_token", "");

        Call<Contests> call = service.getSearchlist(access_token, text, 300);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    contest = response.body();

                    // 검색 결과가 없을 경우, 결과없음 표시
                    if (contest.isResult() == false)
                        sText.setVisibility(View.VISIBLE);
                    else
                        sText.setVisibility(View.INVISIBLE);

                    rec = new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_search2);
                    item = new Recycler_item[contest.getDatasize()];

                    for (int i = 0; i < contest.getDatasize(); i++) {
                        String[] parts = contest.getData(i).getPeriod().split("T");
                        Dday day = new Dday();

                        item[i] = new Recycler_item(contest.getData(i).getTitle(),
                                contest.getData(i).getCont_title(), contest.getData(i).getUsername(),
                                contest.getData(i).getRecruitment(),
                                contest.getData(i).getMembers(),
                                contest.getData(i).getIs_clip(),
                                contest.getData(i).getCategories(), contest.getData(i).getCont_locate(),
                                "D - " + day.dday(parts[0]),
                                contest.getData(i).getContests_id(),
                                contest.getData(i).getCont_writer(),
                                contest.getData(i).getIs_finish()
                        );
                        items.add(item[i]);

                        content.setAdapter(rec);
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

