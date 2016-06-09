package com.ourincheon.wazap.facebook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.MainActivity;
import com.ourincheon.wazap.NotoTextView;
import com.ourincheon.wazap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by YS on 2016-02-19.
 */
public class FacebookLogin extends Activity {
    CallbackManager callbackManager;
    AccessToken accessToken;
    TextView anonymousLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //    getHash();
        /* android app key 생성
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }*/

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);



        callbackManager = CallbackManager.Factory.create();

        // If the access token is available already assign it.
      //  accessToken = AccessToken.getCurrentAccessToken();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        if(!pref.getString("name", "").equals(""))
        {
            loginComplete();
            finish();
        }

        anonymousLogin = (NotoTextView) findViewById(R.id.anonymous_login);
        anonymousLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginComplete();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken = AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                       try {
//                                            Log.d("test : ", object.getString("name"));
                                           SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                           SharedPreferences.Editor editor = pref.edit();

                                           // userName 저장
                                           editor.putString("name", object.getString("name"));
                                           editor.commit();

                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }
                                        Log.d("test : ", response.toString());
                                        setUserInfo(object);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // cancel code
                        Toast.makeText(getApplicationContext(), "Login cancel", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // error code
                        Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_LONG);
                    }
                });
    }
    private void getHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    // 사용자 정보 저장 프로세스
    void setUserInfo(JSONObject object) {
        try {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            // 사용자 id 저장
            String user_id = object.getString("id");
            editor.putString("user_id", user_id);

            // 사진 URL 저장, 파라미터로 small, normal, large, square 넣어도 됨
            URL url = new URL("https://graph.facebook.com/"+user_id+"/picture?type=normal");
            editor.putString("profile_img", url.toString());

            // Access_token 저장
            editor.putString("access_token", accessToken.getToken());
            editor.commit();

            System.out.println("------------------"+pref.getString("access_token",""));
            String baseUrl = "http://come.n.get.us.to";
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HttpService httpService = client.create(HttpService.class);

            Call<LinkedTreeMap> call = httpService.setUserInfo(
                    accessToken.getToken(),
                    user_id,
                    object.getString("name"),
                    url.toString()
            );
            call.enqueue(new Callback<LinkedTreeMap>() {
                @Override
                public void onResponse(Response<LinkedTreeMap> response) {
                    if (response.isSuccess() && response.body() != null) {
                        LinkedTreeMap temp = response.body();

                        boolean result = Boolean.parseBoolean(temp.get("result").toString());
                        String msg = temp.get("msg").toString();

                        if (result) {
                            Log.d("저장 결과: ", msg);
                            Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG);

                        } else {
                            Log.d("저장 실패: ", msg);
                        }
                        loginComplete();
                        finish();
                    } else if (response.isSuccess())
                        Log.d("Response Body is NULL", response.message());
                    else {
                        Log.d("Response Error Body", response.errorBody().toString());
                        System.out.println(response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("Error", t.getMessage());
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //loginComplete();
    }

    void loginComplete() {
        // 완료후 액티비티 이동
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

}
