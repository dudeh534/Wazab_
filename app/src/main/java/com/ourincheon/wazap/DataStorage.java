package com.ourincheon.wazap;

/**
 * Created by Youngdo on 2016-02-02.
 */
public class DataStorage {
    private int position;

    private static DataStorage m_instance;
    public static DataStorage getInstance() {
        if (m_instance == null) {
            m_instance = new DataStorage();
        }
        return m_instance;
    }

    public int getPosition(){
        return position;
    }

    public void setPosition(int position){
        this.position = position;
    }

}
