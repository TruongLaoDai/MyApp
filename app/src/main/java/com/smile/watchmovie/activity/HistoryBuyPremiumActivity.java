package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smile.watchmovie.R;
import com.smile.watchmovie.adapter.HistoryUpVipAdapter;
import com.smile.watchmovie.databinding.ActivityHistoryBuyPremiumBinding;
import com.smile.watchmovie.model.HistoryUpVip;

import java.util.ArrayList;
import java.util.List;

public class HistoryBuyPremiumActivity extends AppCompatActivity {

    private ActivityHistoryBuyPremiumBinding binding;
    private HistoryUpVipAdapter historyUpVipAdapter;
    private List<HistoryUpVip> historyUpVipList;
    private String idUser;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_buy_premium);

        binding = ActivityHistoryBuyPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        historyUpVipList = new ArrayList<>();
        historyUpVipAdapter = new HistoryUpVipAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rcvHistoryBuyPremium.setLayoutManager(linearLayoutManager);
        binding.rcvHistoryBuyPremium.addItemDecoration(itemDecoration);

        historyUpVipAdapter.setData(historyUpVipList);
        binding.rcvHistoryBuyPremium.setAdapter(historyUpVipAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("WatchFilm");

        binding.ivBack.setOnClickListener(v -> finish());

        callApiGetListHistoryUpVip();
    }

    private void callApiGetListHistoryUpVip() {
        collectionReference.document("tblhistoryupvip").collection("user" + idUser).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot.size() > 0) {
                            for (QueryDocumentSnapshot doc : snapshot) {
                                binding.loadHistoryPayment.setVisibility(View.GONE);
                                binding.ivEmpty.setVisibility(View.GONE);
                                HistoryUpVip historyUpVip = doc.toObject(HistoryUpVip.class);
                                historyUpVipList.add(historyUpVip);
                            }
                            historyUpVipAdapter.notifyDataSetChanged();
                        } else {
                            binding.loadHistoryPayment.setVisibility(View.VISIBLE);
                            binding.ivEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                    binding.loadHistoryPayment.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    binding.loadHistoryPayment.setVisibility(View.INVISIBLE);
                    binding.loadHistoryPayment.setVisibility(View.VISIBLE);
                    binding.ivEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(HistoryBuyPremiumActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                });
    }
}