package com.ourincheon.wazap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ourincheon.wazap.facebook.FacebookLogin;

/**
 * Created by Youngdo on 2016-01-19.
 */
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent(Splash.this, FacebookLogin.class);
                startActivity(intent);
                finish();
            }
        };

        handler.sendEmptyMessageDelayed(0, 3000);
    }
}
