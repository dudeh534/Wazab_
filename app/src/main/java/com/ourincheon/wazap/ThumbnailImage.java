package com.ourincheon.wazap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by sue on 2016-02-21.
 */
public class ThumbnailImage extends AsyncTask<String, Void, Bitmap> {
    private String thumbnail;
    private ImageView profileImg;

    public ThumbnailImage() {
    }

    public ThumbnailImage(String thumbnail, ImageView profileImg) {
        this.thumbnail = thumbnail;
        this.profileImg = profileImg;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // TODO Auto-generated method stub

        try {
            URL url = new URL(thumbnail);
            InputStream is = url.openConnection().getInputStream();
            Bitmap bitMap = BitmapFactory.decodeStream(is);
            return bitMap;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    protected void onPostExecute(Bitmap result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        profileImg.setImageBitmap(result);
    }
}