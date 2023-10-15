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

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.smile.watchmovie.eventBus.EventNotifyLogIn;
import com.smile.watchmovie.activity.ChoosePaymentActivity;
import com.smile.watchmovie.activity.FavoriteFilmActivity;
import com.smile.watchmovie.activity.HistoryWatchFilmActivity;
import com.smile.watchmovie.activity.InfoAccountActivity;
import com.smile.watchmovie.activity.LoginActivity;
import com.smile.watchmovie.R;
import com.smile.watchmovie.activity.PrivateSettingActivity;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.FragmentPersonBinding;
import com.smile.watchmovie.model.WeatherResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonFragment extends Fragment {
    private FragmentPersonBinding binding;
    String nameUser, idUser, is_vip;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        getInfoUserInDB();
        setupInfo();
        onClickItem();
        showWeather();

        return binding.getRoot();
    }

    private void setupInfo() {
        if (!is_vip.equals("0")) {
            binding.ivVip.setVisibility(View.VISIBLE);
            binding.tvTitlePayDetail.setText(R.string.account_vip);
        } else {
            binding.ivVip.setVisibility(View.GONE);
            binding.tvTitlePayDetail.setText(R.string.buy_title);
        }

        if (!nameUser.equals("")) {
            binding.tvNameAccount.setText(nameUser);
            binding.ivAvtAccount.setVisibility(View.VISIBLE);
            loadAvatarUser();
        } else {
            binding.ivAvtAccount.setVisibility(View.GONE);
            binding.tvNameAccount.setText(requireActivity().getString(R.string.login));
        }
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
                Toast.makeText(requireActivity(), getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
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

        binding.tvHelpFeedback.setOnClickListener(v ->
                Toast.makeText(requireActivity(), getString(R.string.feature_deploying), Toast.LENGTH_SHORT).show()
        );

        binding.loutPay.setOnClickListener(v -> {
            if (idUser == null || idUser.equals("")) {
                Toast.makeText(requireActivity(), getString(R.string.not_logged_in_message), Toast.LENGTH_SHORT).show();
            } else {
                requireActivity().startActivity(new Intent(requireActivity(), ChoosePaymentActivity.class));
            }
        });

        binding.loutAccount.setOnClickListener(v -> {
            if (idUser.equals("")) {
                startActivity(new Intent(requireActivity(), LoginActivity.class));
            } else {
                startActivity(new Intent(requireActivity(), InfoAccountActivity.class));
            }
        });
    }

    public void showWeather() {
        ApiService.apiWeather.getWeather("Hanoi", "metric", requireActivity().getString(R.string.appId)).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                WeatherResponse weatherResponse = response.body();
                if (weatherResponse != null) {
                    binding.tvWeather.setText(weatherResponse.getMain().getTemp() + "°C" + " tại Hà Nội");
                    binding.tvWeatherDetail.setText(weatherResponse.getWeather().get(0).getMain());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventNotifyLogIn isLogIn) {
        getInfoUserInDB();
        setupInfo();
    }

    private void getInfoUserInDB() {
        idUser = sharedPreferences.getString("idUser", "");
        nameUser = sharedPreferences.getString("name", "");
        is_vip = sharedPreferences.getString("isVip", "0");
    }

    private void loadAvatarUser() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(requireActivity());
        if (signInAccount != null) {
            Glide.with(this).load(signInAccount.getPhotoUrl()).into(binding.ivAvtAccount);
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