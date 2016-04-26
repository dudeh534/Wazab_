package com.ourincheon.wazap.Retrofit;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsue on 16. 2. 19.
 */
public class regUser
{
    boolean result;
    String msg;
    List<JSONData> data = new ArrayList<JSONData>();

    public boolean isResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }


}

class JSONData{
    public String username;
    String major;
    String school;
    String locate;
    String kakao_id;
    String introduce;
    String exp;
    String skill;
    String profile_img;
    int age;
}