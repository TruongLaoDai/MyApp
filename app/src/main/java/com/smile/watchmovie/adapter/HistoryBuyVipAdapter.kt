package com.smile.watchmovie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smile.watchmovie.R
import com.smile.watchmovie.databinding.ItemHistoryPaymentBinding
import com.smile.watchmovie.model.HistoryUpVip
import com.smile.watchmovie.utils.OtherUtils

class HistoryBuyVipAdapter(val context: Context) :
    RecyclerView.Adapter<HistoryBuyVipAdapter.ViewHolder>() {
    private var list = ArrayList<HistoryUpVip>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<HistoryUpVip>) {
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHistoryPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.apply {
                    tvTypePremium.text = if (this@with.is_vip == "1") {
                        "Gói tháng"
                    } else {
                        "Gói năm"
                    }

                    tvDateStart.text =
                        context.getString(R.string.date_register, this@with.dateRegister)

                    tvStatus.text = context.getString(
                        R.string.status,
                        OtherUtils().formatCommonTime(this@with.dateRegister, this@with.is_vip)
                    )
                }
            }
        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val binding: ItemHistoryPaymentBinding) :
        RecyclerView.ViewHolder(binding.root)
}