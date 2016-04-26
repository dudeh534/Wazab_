package com.ourincheon.wazap.Retrofit;

import android.util.Log;

import com.ourincheon.wazap.WazapService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;

/**
 * Created by hsue on 16. 2. 19.
 */
public class RetrofitService {

    regUser reguser;

    public void loadPage()
    {

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<regUser> call = service.getUserInfo("1");
        call.enqueue(new Callback<regUser>() {
            @Override
            public void onResponse( Response<regUser> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    reguser = response.body();
                    //user = response.body();
                    Log.d("SUCCESS", "");
                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure( Throwable t) {
                t.printStackTrace();
                Log.e("Errorglg''';kl", t.getMessage());
            }
        });
    }

}
