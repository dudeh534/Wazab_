package com.ourincheon.wazap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.ourincheon.wazap.facebook.FacebookLogin;

/**
 * Created by YS on 2016-05-26.
 */
public class Setting extends AppCompatActivity {

    Button logoutBtn;
    ToggleButton pushOnOffBtn;
    ImageButton introButton;
    CallbackManager callbackManager;
    Button jBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        logoutBtn = (Button) findViewById(R.id.logout_button);
        pushOnOffBtn = (ToggleButton) findViewById(R.id.push_onoff);
        introButton = (ImageButton) findViewById(R.id.intro_button);

        try {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            boolean isPush = pref.getBoolean("push", true);
            pushOnOffBtn.setChecked(isPush);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Push on/off 설정 저장
        pushOnOffBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("push", isChecked);
                editor.apply();

                // TODO push on/off actions
            }
        });

        // 개발자 소개로 이동
        introButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, Introduce.class);
                startActivity(intent);
            }
        });

        // Logout 한뒤 첫 로그인 페이지로 이동
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("name", "");
                editor.apply();

                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();

                LoginManager.getInstance().logOut();

                Intent intent=new Intent(Setting.this, FacebookLogin.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // 뒤로가는 버튼 누를 경우
        jBefore = (Button) findViewById(R.id.aBefore);
        jBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("클릭", "안되니");
                finish();
            }
        });
    }
}
