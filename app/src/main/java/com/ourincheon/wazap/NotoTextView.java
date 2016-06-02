package com.ourincheon.wazap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Youngdo on 2016-02-27.
 */
public class NotoTextView extends TextView {
    public NotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public NotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotoTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NotoTextView(Context context) {
        super(context);
    }
    private void setType(Context context){
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"NotoSansKR-Regular-Hestia.otf"));
    }
}
