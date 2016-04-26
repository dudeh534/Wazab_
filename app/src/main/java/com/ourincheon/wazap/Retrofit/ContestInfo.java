package com.ourincheon.wazap.Retrofit;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sue on 2016-02-20.
 */
public class ContestInfo {

    /*
    @SerializedName("access_token")
    String access_token;
    */
    @SerializedName("title")
    String title;
    @SerializedName("hosts")
    String hosts;
    @SerializedName("categories")
    ArrayList<String> categories;
    //String categories;
    @SerializedName("period")
    String period;
    @SerializedName("cover")
    String cover;
    @SerializedName("positions")
    String positions;
    @SerializedName("recruitment")
    int recruitment;
    @SerializedName("cont_locate")
    String cont_locate;
    @SerializedName("cont_title")
    String cont_title;


    public ContestInfo(){
        //categories = new JSONArray();
        categories = new ArrayList<String>();
    }

    public void setCont_title(String cont_title) {
        this.cont_title = cont_title;
    }

    public String getCont_locate() {
        return cont_locate;
    }

    public void setCont_locate(String cont_locate) {
        this.cont_locate = cont_locate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public void setCategories(String category) {
        categories.add(category);
        // categories.put(category);

        //String str = categories.toString();
        /*try {
            categories = new JSONArray(category);
            System.out.print("-----------"+categories);
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
*/System.out.print("-----------"+category);
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public void setRecruitment(int recruitment) {
        this.recruitment = recruitment;
    }
}
