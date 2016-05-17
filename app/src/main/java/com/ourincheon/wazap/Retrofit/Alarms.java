package com.ourincheon.wazap.Retrofit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsue on 16. 2. 25.
 */
public class Alarms
{
    boolean result;
    String msg;
    List<AlarmData> data = new ArrayList<AlarmData>();

    public boolean isResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public AlarmData getData(int i)
    {
        return data.get(i);
    }

}
