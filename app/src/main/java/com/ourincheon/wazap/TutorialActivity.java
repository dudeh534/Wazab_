package com.ourincheon.wazap;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Youngdo on 2016-06-23.
 */
public class TutorialActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TutorialAdapter adapter = new TutorialAdapter(this, getLayoutInflater());
        viewPager.setAdapter(adapter);
    }
}
