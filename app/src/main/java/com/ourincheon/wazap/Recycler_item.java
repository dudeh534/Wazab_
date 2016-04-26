package com.ourincheon.wazap;

/**
 * Created by Youngdo on 2016-01-19.
 */
public class Recycler_item {

    private String title;
    private String text;
    private String name;
    private String category;
    private String loc;
    private String day;
    private String writer;
    private int id;
    private int recruit;
    private int member;
    private int clip;
    private int finish;
    private int click;


    String getTitle(){
        return this.title;
    }

    String getText(){
        return this.text;
    }
    String getName(){
        return this.name;
    }

    public String getDay() {
        return day;
    }

    public String getCategory() {
        return category;
    }

    public String getLoc() {
        return loc;
    }

    public int getRecruit() {
        return recruit;
    }

    public int getMember() {
        return member;
    }

    public int getClip() {
        return clip;
    }

    public int getFinish() {
        return finish;
    }

    public String getWriter() {
        return writer;
    }

    public int getId() {
        return id;
    }

    public int getClick() {
        return click;
    }

    public void setClick() {
        this.click = click+1;
    }

    Recycler_item(String title, String text, String name, int recruit,int member,int clip,String category, String loc, String day, int id, String writer, int finish)
     {
        this.title=title;
        this.text=text;
        this.name=name;
        this.recruit = recruit;
        this.member = member;
        this.clip = clip;
        this.category = category;
        this.loc = loc;
        this.day = day;
        this.id = id;
        this.writer = writer;
         this.finish = finish;
         click = 0;
    }
}
