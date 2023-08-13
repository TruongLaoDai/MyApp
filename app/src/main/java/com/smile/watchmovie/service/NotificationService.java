package com.smile.watchmovie.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.MainActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.model.TemperatureHumidity;
import com.smile.watchmovie.model.Weather;
import com.smile.watchmovie.model.WeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ApiService.apiWeather.getWeather("Hanoi", "metric", getApplicationContext().getString(R.string.appId)).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                WeatherResponse weatherResponse = response.body();
                if (weatherResponse != null) {
                    if (weatherResponse.getMain() != null) {
                        TemperatureHumidity main = weatherResponse.getMain();
                        List<Weather> weathers = weatherResponse.getWeather();
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1,
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "watchmedia")
                                .setSmallIcon(R.drawable.ic_weather)
                                .setContentTitle(main.getTemp() + "°C" + " in Ha Noi")
                                .setContentText(weathers.get(0).getMain())
                                .setAutoCancel(true)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentIntent(pendingIntent);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }

                        notificationManagerCompat.notify(123, builder.build());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
            }
        });
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
