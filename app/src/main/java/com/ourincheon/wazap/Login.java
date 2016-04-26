package com.ourincheon.wazap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Youngdo on 2016-01-19.
 */
public class Login extends Activity {

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    EditText id, passwd;
    Button Login, signup;
    CheckBox AutoLogin;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        id = (EditText) findViewById(R.id.editText);
        passwd = (EditText) findViewById(R.id.editText2);

        Login = (Button) findViewById(R.id.button);

        signup = (Button) findViewById(R.id.button2);
        AutoLogin = (CheckBox) findViewById(R.id.checkBox);
        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // loadItem(
              //  startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(Login.this, SignUpActivity.class);
                startActivity(signup);
            }
        });
    }
    /*
        public void loadItem() {
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://kauth.kakao.com/").addConverterFactory(GsonConverterFactory.create()).build();

            WazapService service = retrofit.create(WapService.class);
    /*
            Callback<KaKaoReturn> call = new Callback<KaKaoReturn>() {
                @Override
                public void onResponse(Response<KaKaoReturn> response) {
                    Log.d("RESPONSE", String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("Error", t.getMessage());
                }
            };
            48aece778b41f5cb55522560cc2ec674

        Call<KaKaoReturn> call = service.getKakao("0a02b5ed836ae2f73a4d8bcb89a0aca1", "http://come.n.get.us.to/kakao_oauth");
        call.enqueue(new Callback<KaKaoReturn>() {
           @Override
           public void onResponse(Response<KaKaoReturn> response)
           {
               Log.d("RESPONSE", String.valueOf(response.code()));
           }

           @Override
           public void onFailure(Throwable t) {
               Log.e("Error", t.getMessage());

           }
        });

    }
*/
   /* void getAccessCode(KaKaoReturn msg)
    {
        Retrofit retrofit2 = new Retrofit.Builder()
            .baseUrl("http://come.n.get.us.to").addConverterFactory(GsonConverterFactory.create()).build();

        WazapService service2 = retrofit2.create(WazapService.class);
        Call<ServerReturn> call = service2.getAccess(msg.getCode());
        call.enqueue(new Callback<ServerReturn>() {
            @Override
            public void onResponse(Response<ServerReturn> response) {
                if (response.isSuccess() && response.body() != null) {
                    serverreturn = response.body();


                    Log.d("Success", serverreturn.getMsg());
                  // redirectMainActivity();
                }
                else if(response.isSuccess())
                    Log.d("Response Body is NULL", response.message());
                else
                    Log.d("Response Error Body",response.errorBody().toString());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }
*/

}
