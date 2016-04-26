package com.ourincheon.wazap.Retrofit;

import com.facebook.FacebookRequestError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sue on 2016-02-24.
 */

public class ContestData  implements Serializable {
    int applies_id;
    int contests_id;
    int recruitment;
    String title;
    String cont_title;
    String cont_writer;
    String hosts;
    String username;
    String categories;
    // List<CateData>
    String period;
    String cover;
    String positions;
    String cont_locate;
    String kakao_id;
    String profile_img;
    int is_apply;
    int members;
    int appliers;
    int clips;
    int views;
    int is_finish;
    int is_clip;

    List<MemberList> member_list = new ArrayList<MemberList>();

    public MemberList getMemberList(int i)
    {
        return member_list.get(i);
    }

    public int getMembersize()
    {return  member_list.size();}

    public String getKakao_id() {
        return kakao_id;
    }

    public void setKakao_id(String kakao_id) {
        this.kakao_id = kakao_id;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public int getIs_apply() {
        return is_apply;
    }

    public void setIs_apply(int is_apply) {
        this.is_apply = is_apply;
    }

    public ContestData(){}
    public int getIs_finish() {
        return is_finish;
    }

    public String getCont_title() {
        return cont_title;
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

    public int getIs_clip() {
        return is_clip;
    }

    public void setIs_clip(int is_clip) {
        this.is_clip = is_clip;
    }

    public void setIs_finish(int is_finish) {
        this.is_finish = is_finish;
    }

    public int getApplies_id() {
        return applies_id;
    }

    public void setApplies_id(int applies_id) {
        this.applies_id = applies_id;
    }

    public int getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(int recruitment) {
        this.recruitment = recruitment;
    }

    public int getContests_id() {
        return contests_id;
    }

    public void setContests_id(int contests_id) {
        this.contests_id = contests_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCont_writer() {
        return cont_writer;
    }

    public void setCont_writer(String cont_writer) {
        this.cont_writer = cont_writer;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /*
    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

*/

    public String getCates() {
        return categories;
    }

     public String getCategories() {
     String str="";
        String[] temp;
         categories = categories.substring(1,categories.length()-1);
        temp = categories.split("\"");
         for(int i=0; i<temp.length; i++)
             str += temp[i]+" ";

        return str;
     }

    public void setCategories(String categories) {
            this.categories = categories;
        }

    /*

    public String getCateStr()
    {
        String str="";
        for(int i=0; i<categories.size(); i++) {
            if (categories.get(i).equals(Category_Arr[0]))
            {
                str += Category_Arr[0]+" ";
                category_idx[0]=1;
            }
            else if (categories.get(i).equals(Category_Arr[1]))
            {
                str += Category_Arr[1]+" ";
                category_idx[1]=1;
            }
            else if (categories.get(i).equals(Category_Arr[2]))
            {
                str += Category_Arr[2]+" ";
                category_idx[2]=1;
            }
            else if (categories.get(i).equals(Category_Arr[3]))
            {
                str += Category_Arr[3]+" ";
                category_idx[3]=1;
            }
            else if (categories.get(i).equals(Category_Arr[4]))
            {
                str += Category_Arr[4]+" ";
                category_idx[4]=1;
            }
            else
            {
                str += Category_Arr[5]+" ";
                category_idx[5]=1;
            }
        }
        for(int j=0; j<6; j++)
            System.out.print(category_idx[j]+" ");
        return str;
    }
*/
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getAppliers() {
        return appliers;
    }

    public void setAppliers(int appliers) {
        this.appliers = appliers;
    }

    public int getClips() {
        return clips;
    }

    public void setClips(int clips) {
        this.clips = clips;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getTitle()
    { return title; }


}




