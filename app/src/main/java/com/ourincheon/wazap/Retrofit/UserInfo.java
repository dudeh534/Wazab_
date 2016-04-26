package com.ourincheon.wazap.Retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hsue
 */
public class UserInfo {

   /* @SerializedName("access-token")
    String access_token;
    */
    @SerializedName("kakao_id")
    String kakao_id;
    @SerializedName("username")
    String username;
    @SerializedName("school")
    String school;
    @SerializedName("age")
    int age;
    @SerializedName("major")
    String major;
    @SerializedName("skill")
    String skill;
    @SerializedName("locate")
    String locate;
    @SerializedName("introduce")
    String introduce;
    @SerializedName("exp")
    String exp;

    public UserInfo(){};
    public UserInfo(String kakao_id, String username, String school, int age, String major, String skill ,String locate, String introduce, String exp)
    {
        this.kakao_id = kakao_id;
        this.username = username;
        this.school = school;
        this.age = age;
        this.major = major;
        this.locate = locate;
        this.introduce = introduce;
        this.exp = exp;
        this.skill = skill;
    }

    public String getSkill() {
        return skill;
    }


    public String getKakao_id() {
        return kakao_id;
    }

    public String getUsername() {
        return username;
    }

    public String getSchool() {
        return school;
    }

    public int getAge() {
        return age;
    }

    public String getMajor() {
        return major;
    }

    public String getLocate() {
        return locate;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getExp() {
        return exp;
    }
}
