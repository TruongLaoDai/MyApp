package com.smile.watchmovie.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.smile.watchmovie.R;
import com.smile.watchmovie.notification.NotificationApplication;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public static final String TAG = FirebaseMessagingService.class.getName();
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        //Data message
        Map<String, String> data = message.getData();

        String msgTitle = data.get("");
        String body = data.get("");

        sendNotification(msgTitle, body);
    }

    private void sendNotification(String msgTitle, String body) {

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, NotificationApplication.CHANNEL_ID)
                .setContentTitle(msgTitle)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_weather);

        Notification notification = noBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.e(TAG, token);
    }
}
