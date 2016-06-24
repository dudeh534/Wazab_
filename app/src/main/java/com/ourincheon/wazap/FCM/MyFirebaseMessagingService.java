package com.ourincheon.wazap.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ourincheon.wazap.MainActivity;
import com.ourincheon.wazap.R;

import java.util.Map;

/**
 * Created by YS on 2016-06-24.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData());
        Map<String, String> message = remoteMessage.getData();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        onBtnNotification(pref.getString("name", "") + message.get("message"));
    }

    // 일반 알림.
    public void onBtnNotification(String message) {
        Log.d(TAG, "노티 띄우기");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        // 아이콘 이미지.
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(bm);

        // 알림이 출력될 때 상단에 나오는 문구.
        builder.setTicker("와서잡아 새소식");

        // 알림 출력 시간.
        builder.setWhen(System.currentTimeMillis());

        // 알림 제목.
        builder.setContentTitle("팀모집에 새로운 소식이 있어요.");

        // 알림 내용.
        builder.setContentText("눌러서 확인하러가기");

        // 알림시 사운드, 진동, 불빛을 설정 가능.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        // 알림 터치시 반응.
        builder.setContentIntent(pendingIntent);

        // 알림 터치시 반응 후 알림 삭제 여부.
        builder.setAutoCancel(true);

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(123456, builder.build());
    }
}
