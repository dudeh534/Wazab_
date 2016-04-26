package com.ourincheon.wazap.Retrofit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sue on 2016-03-20.
 */
public class WeeklyList {
    boolean result;
    String msg;
    List<WeeklyData> data = new ArrayList<WeeklyData>();

    public boolean isResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public WeeklyData getData(int i)
    {
        return data.get(i);
    }

    public int getDatasize()
    {return  data.size();}

}
