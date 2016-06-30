package com.ourincheon.wazap;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Youngdo on 2016-06-23.
 */
public class TutorialAdapter extends PagerAdapter {

    LayoutInflater inflater;
    Activity parent;

    public TutorialAdapter(Activity parent, LayoutInflater inflater){
        this.parent = parent;
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
                img.setBackgroundResource(R.drawable.tutorial_first);
                break;
            case 1:
                img.setBackgroundResource(R.drawable.tutorial_second);
                break;
            case 2:
                NotoTextView startBtn = (NotoTextView) view.findViewById(R.id.wazap_start_btn);
                startBtn.setVisibility(View.VISIBLE);
                Animation anima = AnimationUtils.loadAnimation(view.getContext(), R.anim.start_btn_up);
                startBtn.startAnimation(anima);
                startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.finish();
                    }
                });
                img.setBackgroundResource(R.drawable.tutorial_third);
                break;
            default:
                break;
        }
        container.addView(view);
        return view;
    }
}
