package com.smile.watchmovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smile.watchmovie.databinding.ItemFilmDetailBinding;
import com.smile.watchmovie.databinding.ItemHistoryPaymentBinding;
import com.smile.watchmovie.model.FilmMainHome;
import com.smile.watchmovie.model.HistoryUpVip;
import com.smile.watchmovie.utils.OtherUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryUpVipAdapter extends RecyclerView.Adapter<HistoryUpVipAdapter.HistoryUpVipViewHolder> {

    private final Context context;
    private List<HistoryUpVip> historyUpVipList;

    public HistoryUpVipAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<HistoryUpVip> historyUpVipList) {
        this.historyUpVipList = historyUpVipList;
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

        if(historyUpVip.getIs_vip().equals("1")) {
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
        if(historyUpVipList != null) {
            return historyUpVipList.size();
        }
        return 0;
    }

    public static class HistoryUpVipViewHolder extends RecyclerView.ViewHolder {

        private ItemHistoryPaymentBinding binding;

        public HistoryUpVipViewHolder(@NonNull ItemHistoryPaymentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
