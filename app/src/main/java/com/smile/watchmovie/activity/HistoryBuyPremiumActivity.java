package com.smile.watchmovie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.HistoryUpVipAdapter;
import com.smile.watchmovie.api.ApiService;
import com.smile.watchmovie.databinding.ActivityChoosePaymentBinding;
import com.smile.watchmovie.databinding.ActivityHistoryBuyPremiumBinding;
import com.smile.watchmovie.model.FilmArrayResponse;
import com.smile.watchmovie.model.HistoryUpVip;
import com.smile.watchmovie.model.HistoryUpVipResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryBuyPremiumActivity extends AppCompatActivity {

    private ActivityHistoryBuyPremiumBinding binding;
    private HistoryUpVipAdapter historyUpVipAdapter;
    private List<HistoryUpVip> historyUpVipList;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_buy_premium);

        binding = ActivityHistoryBuyPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        historyUpVipList = new ArrayList<>();
        historyUpVipAdapter = new HistoryUpVipAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvHistoryBuyPremium.setLayoutManager(linearLayoutManager);
        binding.rcvHistoryBuyPremium.addItemDecoration(itemDecoration);

        historyUpVipAdapter.setData(historyUpVipList);
        binding.rcvHistoryBuyPremium.setAdapter(historyUpVipAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        binding.ivBack.setOnClickListener(v -> finish());

        callApiGetListHistoryUpVip();
    }

    private void callApiGetListHistoryUpVip() {
        ApiService.apiUser.getListHistoryToVip(idUser).enqueue(new Callback<HistoryUpVipResponse>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<HistoryUpVipResponse> call, @NonNull Response<HistoryUpVipResponse> response) {
                HistoryUpVipResponse historyUpVipResponse = response.body();
                if(historyUpVipResponse != null) {
                    if(historyUpVipResponse.getMessage().equals("Success")) {
                        if(historyUpVipResponse.getData().size() > 0) {
                            binding.loadHistoryPayment.setVisibility(View.GONE);
                            binding.ivEmpty.setVisibility(View.GONE);
                            historyUpVipList.addAll(historyUpVipResponse.getData());
                            historyUpVipAdapter.notifyDataSetChanged();
                        } else {
                            binding.loadHistoryPayment.setVisibility(View.VISIBLE);
                            binding.ivEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HistoryUpVipResponse> call, @NonNull Throwable t) {
                Toast.makeText(HistoryBuyPremiumActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}