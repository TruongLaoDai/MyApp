package com.smile.watchmovie.activity;

import androidx.appcompat.app.AppCompatActivity;

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
        binding = ActivityHistoryBuyPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        historyUpVipList = new ArrayList<>();
        historyUpVipAdapter = new HistoryUpVipAdapter();

        historyUpVipAdapter.setData(historyUpVipList);
        binding.rcvHistoryBuyPremium.setAdapter(historyUpVipAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("WatchFilm");

        binding.toolBar.setNavigationOnClickListener(view -> finish());

        callApiGetListHistoryUpVip();
    }

    private void callApiGetListHistoryUpVip() {
        binding.loadHistoryPayment.setVisibility(View.VISIBLE);
        collectionReference.document("tblhistoryupvip").collection("user" + idUser).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.loadHistoryPayment.setVisibility(View.GONE);
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot.size() > 0) {
                            for (QueryDocumentSnapshot doc : snapshot) {
                                historyUpVipList.add(doc.toObject(HistoryUpVip.class));
                            }
                            historyUpVipAdapter.setData(historyUpVipList);
                        } else {
                            binding.tvContent.setVisibility(View.VISIBLE);
                            binding.rcvHistoryBuyPremium.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(this, R.string.get_data_fail, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}