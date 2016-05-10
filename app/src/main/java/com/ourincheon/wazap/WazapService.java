package com.ourincheon.wazap;


import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.KaKao.infoKaKao;
import com.ourincheon.wazap.Retrofit.Alarms;
import com.ourincheon.wazap.Retrofit.Appliers;
import com.ourincheon.wazap.Retrofit.ContestInfo;
import com.ourincheon.wazap.Retrofit.Contests;
import com.ourincheon.wazap.Retrofit.UserInfo;
import com.ourincheon.wazap.Retrofit.WeeklyList;
import com.ourincheon.wazap.Retrofit.regMsg;
import com.ourincheon.wazap.Retrofit.regUser;
import com.ourincheon.wazap.Retrofit.reqContest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WazapService {
    /*
    @GET("oauth/authorize?response_type=code")
    //client_id={app_key}&redirect_uri={redirect_uri}&
    Call<KaKaoReturn> getKakao(
            @Query("client_id") String app_key,
            @Query("redirect_uri") String redirect_uri);

    @GET("/kakao_oauth?")
    Call<ServerReturn> getAccess(
            @Query("code") String code);
            */
    @GET("/kakao_oauth?")
    Call<infoKaKao> getToken(
            @Query("nickname") String name,
            @Query("thumnail") String thumnail);


    // 사용자 정보 저장하기
    @POST("/users/reg")
    Call<regMsg> createInfo(
            @Header("access-token") String access_token,
            @Body UserInfo userInfo);


    // 사용자 정보 받아오기
    @GET("users/{user_id}")
    Call<regUser> getUserInfo(
            @Path("user_id") String user_id
    );

    // 모집글 쓰기
    @POST("contests")
    Call<LinkedTreeMap> createContests(
            @Header("access-token") String access_token,
            @Body ContestInfo contestInfo
    );

    // 메인목록 받아오기
    @GET("contests")
    Call<Contests> getContests(
            @Header("access-token") String access_token,
            @Query("amount") int amount
    );

    // 상세 정보 받아오기
    @GET("contests/{contest_id}")
    Call<reqContest> getConInfo(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token
    );

    // 모집글 신청
    @POST("contests/{contest_id}/join")
    Call<LinkedTreeMap> applyContests(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token

    );

    // 글 찜하기
    @POST("clips/{contest_id}")
    Call<LinkedTreeMap> clipContests(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token
    );

    // 신청목록 받아오기
    @GET("contests/applications")
    Call<Contests> getAppplylist(
            @Header("access-token") String access_token,
            @Query("start_id") int start_id,
            @Query("amount") int amount
    );

    // 알람목록 받아오기
    @GET("alrams")
    Call<Alarms> getAlarmlist(
            @Header("access-token") String access_token,
      //      @Query("start_id") int start_id,
            @Query("amount") int amount
    );

    // 찜목록 받아오기
    @GET("clips")
    Call<Contests> getCliplist(
            @Header("access-token") String access_token,
           // @Query("start_id") int start_id,
            @Query("amount") int amount
    );

    // 게시글 수정하기
    @PUT("contests/{contest_id}")
    Call<LinkedTreeMap> editContest(
            @Header("access-token") String access_token,
            @Path("contest_id") String contest_id,
            @Body ContestInfo contestInfo
    );

    // 게시글 삭제하기
    @DELETE("contests/{contest_id}")
    Call<LinkedTreeMap> delContest(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token
    );

    // 모집목록 받아오기
    @GET("contests/list/{writer_id}")
    Call<Contests> getContestlist(
            @Path("writer_id") String writer_id,
            @Header("access-token") String access_token
    );

    // 신청자목록 받아오기
    @GET("contests/{contest_id}/applies")
    Call<Appliers> getApplierlist(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token
    );

    // 멤버변경하기
    @POST("contests/{contest_id}/{applies_id}")
    Call<LinkedTreeMap> changeMember(
            @Path("contest_id") String contest_id,
            @Path("applies_id") String applies_id,
            @Header("access-token") String access_token
    );

    // 마감하기
    @PUT("contests/finish/{contest_id}")
    Call<LinkedTreeMap> finishContest(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token
            //@Body Access access_token
    );

    // 스크랩 삭제하기
    @DELETE("clips/{contest_id}")
    Call<LinkedTreeMap> delClip(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token
    );

    // 신청글 취소하기
    @DELETE("contests/{contest_id}/join")
    Call<LinkedTreeMap> delApply(
            @Path("contest_id") String contest_id,
            @Header("access-token") String access_token
    );

    // 주간목록 받아오기
    @GET("weekly_list")
    Call<WeeklyList> getWeeklylist(
            @Header("access-token") String access_token,
            @Query("amount") int amount
    );

    // 타이틀명 검색하기
    @GET("search")
    Call<Contests> getSearchlist(
            @Header("access-token") String access_token,
            @Query("search") String search,
           // @Query("start_id") int start_id,
            @Query("amount") int amount
    );

    // 시구 검색하기
    @GET("locate")
    Call<LinkedTreeMap> getLocatelist(
            @Header("access-token") String access_token
    );

    // 군구 검색하기
    @GET("locate/{state_name}")
    Call<LinkedTreeMap> getLocatebasedlist(
            @Header("access-token") String access_token,
            @Path("state_name") String state_name
    );

    // 카테고리 받아오기
    @GET("contests/categories")
    Call<Contests> getContestsByCategory(
            @Header("access-token") String access_token,
            @Query("category_name") String name,
            @Query("amount") int amount
    );
}



