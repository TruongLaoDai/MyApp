package com.smile.watchmovie.notification;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.MainActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.model.TemperatureHumidity;
import com.smile.watchmovie.model.Weather;
import com.smile.watchmovie.model.WeatherResponse;
import com.smile.watchmovie.service.NotificationService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service_intent = new Intent(context, NotificationService.class);

        context.startService(service_intent);
//        ApiService.apiWeather.getWeather("Hanoi", "metric", context.getString(R.string.appId)).enqueue(new Callback<WeatherResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
//                WeatherResponse weatherResponse = response.body();
//                if (weatherResponse != null) {
//                    if (weatherResponse.getMain() != null) {
//                        TemperatureHumidity main = weatherResponse.getMain();
//                        List<Weather> weathers = weatherResponse.getWeather();
//                        Intent  intent1 = new Intent(context, MainActivity.class);
//                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1,
//                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
//                                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT :PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
//
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "watchmedia")
//                                .setSmallIcon(R.drawable.ic_weather)
//                                .setContentTitle(main.getTemp()+"°C"+" in Ha Noi")
//                                .setContentText(weathers.get(0).getMain())
//                                .setAutoCancel(true)
//                                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                .setContentIntent(pendingIntent);
//
//                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//
//                            return;
//                        }
//
//                        notificationManagerCompat.notify(123, builder.build());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
//                //Toast.makeText(MainActivity.this, "Fail to get weather", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


}
