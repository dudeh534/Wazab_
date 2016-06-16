package com.ourincheon.wazap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by Youngdo on 2016-06-16.
 */
public class NotoCheckBox extends CheckBox {
    public NotoCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }
    public NotoCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotoCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NotoCheckBox(Context context) {
        super(context);
    }
    private void setType(Context context){
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "NotoSansKR-Regular-Hestia.otf"));
    }
}
