package com.ourincheon.wazap;

/**
 * Created by hsue on 16. 4. 27.
 */

//*** 모집글 추가,삭제 등 동작시 flag저장 ***//
public class ChangeStatus {
    int deleted;
    int newed;

    private static ChangeStatus m_instance;
    public static ChangeStatus getInstance() {
        if (m_instance == null) {
            m_instance = new ChangeStatus();
        }
        return m_instance;
    }

    public void setDeleted() {
        deleted = 1;
    }

    public void removeDeleted() {
        deleted = 0;
    }

    public int getDeleted()
    {
        return deleted;
    }

    public void setNewed()
    {
        newed = 1;
    }

    public void removeNewed()
    {
        newed = 0;
    }

    public int getNewed()
    {
        return newed;
    }

}
