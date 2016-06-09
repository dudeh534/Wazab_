package com.ourincheon.wazap;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by YS on 2016-06-09.
 */
public class CheckAnonymous {
    // 익명 로그인 확인
    protected static boolean isAnonymous(Context context) {
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);

        String userId = pref.getString("user_id","");
        // 로그인 상태 파악후 반환 (익명 로그인시 true)
        return userId == "";

    }
}
