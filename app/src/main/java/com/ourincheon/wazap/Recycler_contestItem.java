package com.ourincheon.wazap;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by sue on 2016-03-19.
 */

public class Recycler_contestItem implements Serializable
{

    private String title;
    private String host;
    private String dday;
    private String date;
    private String img;
    private String total;
    String target;
    String benefit;
    String prize;
    String homepage;
    String category;


    public String getTarget() {
        return target;
    }

    public String getBenefit() {
        return benefit;
    }

    public String getPrize() {
        return prize;
    }

    public String getHomepage() {
        return homepage;
    }

    String getTitle() {
        return this.title;
    }

    String getHost() {
        return this.host;
    }

    public String getDday() {
        return dday;
    }

    public String getDate() {
        return date;
    }

    public String getImg()
    {
        return img;
    }

    public String getTotal() {
        return total;
    }

    public String getCategory() {
        return category;
    }

    Recycler_contestItem(String title, String host, String dday, String date, String img,
                         String total, String target, String benefit,String prize, String homepage, String category)
    {
        this.title = title;
        this.host = host;
        this.dday = dday;
        this.date = date;
        this.img = img;
        this.total = total;
        this.target = target;
        this.benefit = benefit;
        this.prize = prize;
        this.homepage = homepage;
        this.category = category;
    }

}
