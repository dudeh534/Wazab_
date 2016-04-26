package com.ourincheon.wazap;

import java.util.GregorianCalendar;

/**
 * Created by hsue on 16. 2. 25.
 */
public class Dday {
    String mDday;
    public int dday(String mDday)
    {
        this.mDday = mDday.trim();

        int first = mDday.indexOf("-");
        int last = mDday.lastIndexOf("-");
        int year = Integer.parseInt(mDday.substring(0, first));
        int month = Integer.parseInt(mDday.substring(first + 1, last));
        int day = Integer.parseInt(mDday.substring(last + 1, mDday.length()));

        GregorianCalendar cal = new GregorianCalendar();
        long currentTime = cal.getTimeInMillis() / (1000 * 60 * 60 * 24);
        cal.set(year, month - 1, day);
        int dday = (int) (cal.getTimeInMillis() / (1000 * 60 * 60 * 24) - currentTime);

        return dday;
    }
}
