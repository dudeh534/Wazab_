package com.ourincheon.wazap.Retrofit;

import java.io.Serializable;

/**
 * Created by sue on 2016-03-20.
 */
public class WeeklyData implements Serializable {
    int ID;
    String IMG;
    String TITLE;
    String TAG;
    String HOSTING;
    String SUVERVISION;
    String START_DATE;
    String DEADLINE_DATE;
    String TARGET;
    String BENEFIT;
    String TOTALPRIZE;
    String FIRSTPRIZE;
    String HOMEPAGE;
    String ATTACHMENT;

    public int getID() {
        return ID;
    }

    public String getIMG() {
        return IMG;
    }

    public String getTITLE() {
        return TITLE;
    }

    public String getTAG() {
        return TAG;
    }

    public String getHOSTING() {
        return HOSTING;
    }

    public String getSUVERVISION() {
        return SUVERVISION;
    }

    public String getSTART_DATE() {
        return START_DATE;
    }

    public String getDEADLINE_DATE() {
        return DEADLINE_DATE;
    }

    public String getTARGET() {
        return TARGET;
    }

    public String getBENEFIT() {
        return BENEFIT;
    }

    public String getTOTALPRIZE() {
        return TOTALPRIZE;
    }

    public String getFIRSTPRIZE() {
        return FIRSTPRIZE;
    }

    public String getHOMEPAGE() {
        return HOMEPAGE;
    }

    public String getATTACHMENT() {
        return ATTACHMENT;
    }
}
