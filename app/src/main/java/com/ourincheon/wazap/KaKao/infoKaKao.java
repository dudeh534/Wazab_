package com.ourincheon.wazap.KaKao;

import android.graphics.Bitmap;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by sue on 2016-02-05.
 */
public class infoKaKao implements Serializable {

    private String Kname;
    private String Kthumbnail;

    public infoKaKao(String name, String thumbnail)
    {
        setName(name);
        setThumbnail(thumbnail);
    }

    public void setName(String name)
    {
        Kname = name;
    }

    public String getName()
    {
        return Kname;
    }

    public void setThumbnail(String thumbnail)
    {
        Kthumbnail = thumbnail;
    }

    public String getThumbnail()
    {
        return Kthumbnail;
    }

}

