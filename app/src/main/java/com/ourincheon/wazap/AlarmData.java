package com.ourincheon.wazap;

/**
 * Created by hsue on 16. 2. 25.
 */
import java.text.Collator;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

// alarm 관련 데이터 저장
public class AlarmData {

    public Drawable mIcon;
    String profile_img;
    String username;
    int alram_id;
    String msg;
    String msg_url;
    String alramdate;
    int is_check;


    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Drawable getmIcon() {
        return mIcon;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public int getAlram_id() {
        return alram_id;
    }

    public void setAlram_id(int alram_id) {
        this.alram_id = alram_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg_url() {
        return msg_url;
    }

    public void setMsg_url(String msg_url) {
        this.msg_url = msg_url;
    }

    public String getAlramdate() {
        return alramdate;
    }

    public void setAlramdate(String alramdate) {
        this.alramdate = alramdate;
    }

    public int getIs_check() {
        return is_check;
    }

    public void setIs_check(int is_check) {
        this.is_check = is_check;
    }

}
