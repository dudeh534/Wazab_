package com.ourincheon.wazap;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Youngdo on 2016-06-23.
 */
public class TutorialAdapter extends PagerAdapter {

    LayoutInflater inflater;

    public TutorialAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        view = inflater.inflate(R.layout.child_view,null);
        ImageView img = (ImageView)view.findViewById(R.id.chlidimg);
        switch (position){
            case 0:
                img.setImageResource(R.drawable.first);
                break;
            case 1:
                img.setImageResource(R.drawable.second);
                break;
            case 2:
                img.setImageResource(R.drawable.third);
                break;
            default:
                break;
        }
        container.addView(view);
        return view;
    }
}
