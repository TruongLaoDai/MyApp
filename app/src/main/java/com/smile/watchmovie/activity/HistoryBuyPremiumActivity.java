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
import com.smile.watchmovie.adapter.HistoryBuyVipAdapter;
import com.smile.watchmovie.databinding.ActivityHistoryBuyPremiumBinding;
import com.smile.watchmovie.model.HistoryUpVip;
import com.smile.watchmovie.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class HistoryBuyPremiumActivity extends AppCompatActivity {
    private ActivityHistoryBuyPremiumBinding binding;
    private HistoryBuyVipAdapter adapter;
    private List<HistoryUpVip> listHistoryBuyVip;
    private String idUser;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBuyPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeData();
        getListHistoryBuyVip();
        handleEventClick();
    }

    private void handleEventClick() {
        binding.toolBar.setNavigationOnClickListener(view -> finish());
    }

    private void initializeData() {
        listHistoryBuyVip = new ArrayList<>();
        adapter = new HistoryBuyVipAdapter(this);

        binding.rcvHistoryBuyPremium.setAdapter(adapter);
        binding.rcvHistoryBuyPremium.setHasFixedSize(true);

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.NAME_DATABASE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(Constant.ID_USER, "");

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(Constant.FirebaseFiretore.NAME_DATABASE);
    }

    private void getListHistoryBuyVip() {
        collectionReference.document(Constant.FirebaseFiretore.TABLE_HISTORY_BUY_PREMIUM)
                .collection("user" + idUser)
                .get()
                .addOnCompleteListener(task -> {
                    binding.loadHistoryPayment.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot.size() > 0) {
                            for (QueryDocumentSnapshot doc : snapshot) {
                                listHistoryBuyVip.add(doc.toObject(HistoryUpVip.class));
                            }
                            adapter.updateData(listHistoryBuyVip);
                        } else {
                            binding.tvContent.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, R.string.get_data_fail, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}