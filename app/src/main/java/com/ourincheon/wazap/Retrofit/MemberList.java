package com.ourincheon.wazap.Retrofit;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsue on 16. 3. 23.
 */
public class MemberList {


    String users_id;
    String username;
    String profile_img;

    public String getUsers_id() {
        return users_id;
    }

    public void setUsers_id(String users_id) {
        this.users_id = users_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_img() {
        String img="";
        try {
            img = URLDecoder.decode(profile_img, "EUC_KR");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }



}

