package com.smile.watchmovie.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smile.watchmovie.service.NotificationService;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, NotificationService.class);
        context.startService(service_intent);
    }
}
