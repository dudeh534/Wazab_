package com.ourincheon.wazap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ourincheon.wazap.Retrofit.Contests;
import com.ourincheon.wazap.Retrofit.WeeklyList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Youngdo on 2016-02-02.
 */
public class FragmentPage extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String[] Category={" ","광고/아이디어/마케팅","디자인","사진/UCC","게임/소프트웨어","해외","기타"};
    public static Context mContext;
    RecyclerView content;
    LinearLayout linearLayout;
    private int position;
    Contests contest;
    WeeklyList weekly;
    RecyclerAdapter rec;
    contestRecyclerAdapter conRec;
    List<Recycler_item> items;
    List<Recycler_contestItem> contestItems;
    Recycler_item[] item;
    Recycler_contestItem[] contestItem;
    String access_token;
    SwipeRefreshLayout swipeRefreshLayout;
    private int category_value =0;
    private int start_id;
    private static int ival = 0;
    private static int loadLimit = 5;
    ProgressDialog dialog;

    public static FragmentPage newInstance(int position) {
        FragmentPage f = new FragmentPage();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);

        mContext = this.getActivity();

        // 저장된 값 불러오기 - user_id와 access_token
        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        final String user_id = pref.getString("user_id", "");
        System.out.println(user_id);
        access_token = pref.getString("access_token", "");

        // 모집글 리스트
        items = new ArrayList<>();

        // 공모전 리스트
        contestItems = new ArrayList<>();

        loadPage(access_token);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (position) {
            //*** 모집글 리스트 프래그먼트 ***//
            case 0:
                linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_page,null,true);
                swipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swipeRefresh);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        items = new ArrayList<>();
                        loadPage(access_token);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });


                //* 카테고리 선택 스피너 *//
                LinearLayout linearLayout_spinner = (LinearLayout) linearLayout.findViewById(R.id.linearLayout_mother);
                Spinner spinner = (Spinner) linearLayout_spinner.findViewById(R.id.spinner);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    // 눌렸을시, 해당 카테고리 리스트만 출력
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqq " + position);
                        category_value= position;
                        loadPage(access_token);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });


                content = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                content.setHasFixedSize(true);
                content.setLayoutManager(layoutManager);
                content.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        Log.d("Scroll TEST", "load func : ");
                        loadPageMore(access_token);
                    }
                });

                return linearLayout;


            //*** 공모전 리스트 프래그먼트 ***//
            case 1:
                linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_page, container, false);
                swipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swipeRefresh);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadContest(access_token);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                content = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                content.setHasFixedSize(true);
                content.setLayoutManager(layoutManager1);

                // 공모전 리스트 로드
                loadContest(access_token);

                conRec = new contestRecyclerAdapter(getActivity(), contestItems, R.layout.fragment_page);
                linearLayout.removeAllViews();
                linearLayout.addView(swipeRefreshLayout);
                return linearLayout;

            default:
                return null;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //*** 추가,삭제시 업데이트 ***//
        ChangeStatus status = ChangeStatus.getInstance();
        System.out.println("=========================Refresh " + status.getNewed());
        if(status.getNewed()==1) {          // 추가된 경우
            items = new ArrayList<>();
            loadPage(access_token);
            status.removeNewed();
        }
        else if(status.getDeleted()==1) {   // 삭제된 경우
            items = new ArrayList<>();
            loadPage(access_token);
            status.removeDeleted();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    //*** 공모전 리스트 서버에서 받아옴 ***//
    void loadContest(String access_token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("------------------------" + access_token);
        Call<WeeklyList> call = service.getWeeklylist(access_token, 300);
        call.enqueue(new Callback<WeeklyList>() {
            @Override
            public void onResponse(Response<WeeklyList> response) {
                if (response.isSuccess() && response.body() != null) {

                    weekly = response.body();

                    //String result = new Gson().toJson(weekly);
                    //Log.d("SUCESS-----", result);

                    contestItem = new Recycler_contestItem[weekly.getDatasize()];
                    Dday day = new Dday();
                    // 받아온 내용 저장
                    contestItems.clear();
                    for (int i = 0; i < weekly.getDatasize(); i++) {
                        contestItem[i] = new Recycler_contestItem(weekly.getData(i).getTITLE(),
                                weekly.getData(i).getHOSTING(),
                                "D - " + day.dday(weekly.getData(i).getDEADLINE_DATE()),
                                weekly.getData(i).getSTART_DATE() + " ~ " + weekly.getData(i).getDEADLINE_DATE(),
                                weekly.getData(i).getIMG(),
                                weekly.getData(i).getTOTALPRIZE(),
                                weekly.getData(i).getTARGET(),
                                weekly.getData(i).getBENEFIT(),
                                weekly.getData(i).getFIRSTPRIZE(),
                                weekly.getData(i).getHOMEPAGE(),
                                weekly.getData(i).getTAG()
                        );
                        contestItems.add(contestItem[i]);

                        content.setAdapter(conRec);
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


    //*** 모집글 리스트 서버에서 받아옴 ***//
    void loadPage(String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        //System.out.println("------------------------" +category_value+ access_token);
        Call<Contests> call = service.getContests(access_token, 10);       // 최근것 10개 받아오기

        // 카테고리 값에 따라 검색
        if(category_value!=0)
            call = service.getContestsByCategory(access_token,Category[category_value], 10);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {
                    Log.d("SUCCESS", response.message());
                    contest = response.body();

                    //String result = new Gson().toJson(contest);
                    //Log.d("SUCESS-----", result);

                    item = new Recycler_item[contest.getDatasize()];
                    items.clear();

                    for (int i = 0; i < contest.getDatasize(); i++) {
                        String[] parts = contest.getData(i).getPeriod().split("T");
                        Dday day = new Dday();

                        start_id=contest.getData(i).getContests_id();   // 가장 마지막 값이 start_id에 저장됨 : 나중에 load more를 할때 사용됨

                        // 받아온 값 저장
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

                        rec = new RecyclerAdapter(getActivity(), items, R.layout.fragment_page);
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


    //*** 모집글 리스트 추가로 서버에서 받아옴 ***//    !!!받아오는것은 가능, 레이아웃 그려줄때, 이전에 받아온 내용 유지하고 추가하는식으로
    void loadPageMore(String access_token)
    {
        dialog = ProgressDialog.show(getActivity(), "",
                "로딩 중입니다. 잠시 기다려주세요", true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


        System.out.println("------------------------" + start_id);
        Call<Contests> call = service.getContests(access_token,start_id-1,10);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {
                    Log.d("SUCCESS", response.message());
                    contest = response.body();

                   // String result = new Gson().toJson(contest);
                   // Log.d("SUCESS-----", result);

                    item = new Recycler_item[contest.getDatasize()];
                    //List<Recycler_item> items = new ArrayList<Recycler_item>();
                    //items.clear();
                    for (int i = 0; i < contest.getDatasize(); i++) {
                        String[] parts = contest.getData(i).getPeriod().split("T");
                        Dday day = new Dday();

                        if(contest.getData(i).getContests_id()<start_id)
                            start_id=contest.getData(i).getContests_id();

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
                        // 현재 리스트 items에 새 아이템 추가
                        items.add(item[i]);

                    }

                    // 리사이클러 뷰 마지막에 새로운 아이템 추가
                    RecyclerAdapter curRec = (RecyclerAdapter)content.getAdapter();
                    int cursize = curRec.getItemCount();
                    curRec.notifyItemRangeInserted(cursize, items.size() - 1); // cursize 위치부터 items.size 길이만큼 늘림
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
        dialog.dismiss();
    }
}