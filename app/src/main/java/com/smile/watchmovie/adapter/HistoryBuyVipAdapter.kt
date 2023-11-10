package com.smile.watchmovie.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.databinding.ItemHistoryPaymentBinding;
import com.smile.watchmovie.model.HistoryUpVip;
import com.smile.watchmovie.utils.OtherUtils;

import java.util.ArrayList;
import java.util.List;

public class HistoryUpVipAdapter extends RecyclerView.Adapter<HistoryUpVipAdapter.HistoryUpVipViewHolder> {
    private final List<HistoryUpVip> historyUpVipList = new ArrayList<>();

    public HistoryUpVipAdapter() {
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<HistoryUpVip> historyUpVipList) {
        this.historyUpVipList.addAll(historyUpVipList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryUpVipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryUpVipAdapter.HistoryUpVipViewHolder(ItemHistoryPaymentBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryUpVipViewHolder holder, int position) {
        HistoryUpVip historyUpVip = historyUpVipList.get(position);

        if (historyUpVip == null) {
            return;
        }

        if (historyUpVip.getIs_vip().equals("1")) {
            holder.binding.tvTypePremium.setText("Gói tháng");
        } else {
            holder.binding.tvTypePremium.setText("Gói năm");
        }

        OtherUtils otherUtils = new OtherUtils();
        holder.binding.tvStatus.setText(otherUtils.formatCommonTime(historyUpVip.getDateRegister(), historyUpVip.getIs_vip()));

        holder.binding.tvDateStart.setText(historyUpVip.getDateRegister());
    }

    @Override
    public int getItemCount() {
        return historyUpVipList.size();
    }

    public static class HistoryUpVipViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryPaymentBinding binding;

        public HistoryUpVipViewHolder(@NonNull ItemHistoryPaymentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
