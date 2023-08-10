package com.smile.watchmovie.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smile.watchmovie.EventBus.EventNotifyLogout;
import com.smile.watchmovie.activity.ChoosePaymentActivity;
import com.smile.watchmovie.activity.FavoriteFilmActivity;
import com.smile.watchmovie.activity.HistoryWatchFilmActivity;
import com.smile.watchmovie.activity.InfoAccountActivity;
import com.smile.watchmovie.activity.LoginActivity;
import com.smile.watchmovie.activity.MainActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.PrivateSettingActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentPersonBinding;
import com.smile.watchmovie.model.TemperatureHumidity;
import com.smile.watchmovie.model.Weather;
import com.smile.watchmovie.model.WeatherResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonFragment extends Fragment {
    private FragmentPersonBinding binding;
    private MainActivity mMainActivity;
    String nameUser, idUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainActivity = (MainActivity) getActivity();
        binding = FragmentPersonBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = mMainActivity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        onClickItem();
        showWeather();

        return binding.getRoot();
    }

    private void onClickItem() {
        binding.tvHistory.setOnClickListener(v -> {
            if (idUser == null || idUser.equals("")) {
                Toast.makeText(requireActivity(), getString(R.string.not_logged_in_message), Toast.LENGTH_SHORT).show();
            } else {
                requireActivity().startActivity(new Intent(requireActivity(), HistoryWatchFilmActivity.class));
            }
        });

        binding.tvDownload.setOnClickListener(v ->
                Toast.makeText(mMainActivity, getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );

        binding.tvFavorite.setOnClickListener(v -> {
            if (idUser == null || idUser.equals("")) {
                Toast.makeText(requireActivity(), getString(R.string.not_logged_in_message), Toast.LENGTH_SHORT).show();
            } else {
                requireActivity().startActivity(new Intent(requireActivity(), FavoriteFilmActivity.class));
            }
        });

        binding.tvSetting.setOnClickListener(v ->
                requireActivity().startActivity(new Intent(requireActivity(), PrivateSettingActivity.class))
        );

        binding.tvFeedback.setOnClickListener(v ->
                Toast.makeText(requireActivity(), getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );

        binding.tvHelp.setOnClickListener(v ->
                Toast.makeText(requireActivity(), getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );

        binding.loutPay.setOnClickListener(v -> {
            if (idUser == null || idUser.equals("")) {
                Toast.makeText(requireActivity(), getString(R.string.not_logged_in_message), Toast.LENGTH_SHORT).show();
            } else {
                requireActivity().startActivity(new Intent(requireActivity(), ChoosePaymentActivity.class));
            }
        });
    }

    public void showWeather() {
        ApiService.apiWeather.getWeather("Hanoi", "metric", mMainActivity.getString(R.string.appId)).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                WeatherResponse weatherResponse = response.body();
                if (weatherResponse != null) {
                    if (weatherResponse.getMain() != null) {
                        TemperatureHumidity main = weatherResponse.getMain();
                        binding.tvWeather.setText(main.getTemp() + "°C" + " tại Hà Nội");
                        List<Weather> weathers = weatherResponse.getWeather();
                        binding.tvWeatherDetail.setText(weathers.get(0).getMain());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                //Toast.makeText(MainActivity.this, "Fail to get weather", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = mMainActivity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        nameUser = sharedPreferences.getString("name", "");
        String is_vip = sharedPreferences.getString("isVip", "0");
        if (!is_vip.equals("0")) {
            binding.ivVip.setVisibility(View.VISIBLE);
            binding.tvTitlePayDetail.setText(R.string.account_vip);
        } else {
            binding.ivVip.setVisibility(View.GONE);
            binding.tvTitlePayDetail.setText(R.string.buy_title);
        }
        if (!nameUser.equals(""))
            binding.tvNameAccount.setText(nameUser);
        else
            binding.tvNameAccount.setText(mMainActivity.getString(R.string.login));

        binding.loutAccount.setOnClickListener(v -> {
                    if (idUser.equals("")) {
                        Intent intent = new Intent(mMainActivity, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(mMainActivity, InfoAccountActivity.class));
                    }
                }
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventNotifyLogout isLogout) {
        if (isLogout.isLogout()) {
            idUser = "";
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}