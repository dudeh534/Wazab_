package com.ourincheon.wazap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by Youngdo on 2016-01-19.
 */
public class ItemInfoActivity extends Activity {
    ImageView main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iteminfo);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        main = (ImageView) findViewById(R.id.imageView3);
        switch (position){
            case 0:
                main.setImageResource(R.drawable.open);
                break;
            case 1:
                main.setImageResource(R.drawable.idea);
                break;
            case 2:
                main.setImageResource(R.drawable.startup);
                break;
            case 3:
                main.setImageResource(R.drawable.a);
                break;
            case 4:
                main.setImageResource(R.drawable.a);
                break;
            default:
                main.setImageResource(R.drawable.a);
                break;
        }
    }
}
