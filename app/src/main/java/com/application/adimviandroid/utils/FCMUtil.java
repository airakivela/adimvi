package com.application.adimviandroid.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.application.adimviandroid.R;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.SplashActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMUtil extends FirebaseMessagingService {

    private static int count = 0;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getData().get("body");
        String msg = remoteMessage.getData().get("title");
        String type = remoteMessage.getData().get("type");
        showNotification(title, msg, type);
    }

    private void showNotification(String title, String msg, String type) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("PUSH_NOTIFICATION", "YES");
        SharedUtil.setSharedHasNewPost(false);
        if (msg.contains("$")) {
            intent.putExtra("SALE_NOTIFICATION", "YES");
        }
        if (type.equals("1")) {
            SharedUtil.setSharedHasNewPost(true);
            intent.putExtra("SIGUIENDO_NOTIFICATION", "YES");
        }
        if (type.equals("2")) {
            intent.putExtra(("FOLLOW_CHAT_ROOM"), "YES");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel("ADIMVI_NOTIFICATION", "ADIMVI_NOTIFICATION", importance);
            notificationChannel.setDescription(msg);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[] {
                    100, 200, 300, 400, 500, 400, 300, 200, 400
            });
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ADIMVI_NOTIFICATION");
        builder.setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultUri)
                .setColor(Color.parseColor("#FFD600"))
                .setContentIntent(pendingIntent)
                .setChannelId("ADIMVI_NOTIFICATION")
                .setStyle(new NotificationCompat.BigPictureStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager.notify(count, builder.build());
        count ++;
    }
}
