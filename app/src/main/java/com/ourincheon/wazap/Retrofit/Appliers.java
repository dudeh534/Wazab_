package com.ourincheon.wazap.Retrofit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsue on 16. 2. 26.
 */
public class Appliers {
    boolean result;
    String msg;
    List<ApplierData> data = new ArrayList<ApplierData>();

    public boolean isResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public ApplierData getData(int i)
    {
        return data.get(i);
    }

}