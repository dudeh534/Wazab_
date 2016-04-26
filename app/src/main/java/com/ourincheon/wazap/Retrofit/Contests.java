package com.ourincheon.wazap.Retrofit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsue on 16. 2. 23.
 */
public class Contests
{
    boolean result;
    String msg;
    List<ContestData> data = new ArrayList<ContestData>();

    public boolean isResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public ContestData getData(int i)
    {
        return data.get(i);
    }

    public int getDatasize()
    {return  data.size();}

}
