package com.vn.visafe_android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.R
import com.vn.visafe_android.databinding.ItemProtectWifiBinding
import com.vn.visafe_android.model.ProtectWifiData

class ProtectWifiAdapter(val wifiList: ArrayList<ProtectWifiData>, val context: Context) :
    RecyclerView.Adapter<ProtectWifiAdapter.ProtectWifiViewHolder>() {
    private var mOnClickListener: OnClickWifi? = null

    fun setOnClickListener(onConfirmListener: OnClickWifi) {
        mOnClickListener = onConfirmListener
    }

    class ProtectWifiViewHolder private constructor(val binding: ItemProtectWifiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProtectWifiData, context: Context) {
            binding.data = item
            binding.ivWifi.setImageResource(
                if (item.isProtected) {
                    R.drawable.ic_checkmark_circle
                } else {
                    R.drawable.ic_info_circle
                }
            )
            binding.tvContent.text = context.getString(R.string.da_chan, item.day.toString())
        }

        companion object {
            fun from(parent: ViewGroup): ProtectWifiViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProtectWifiBinding.inflate(layoutInflater, parent, false)
                return ProtectWifiViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProtectWifiViewHolder {
        return ProtectWifiViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProtectWifiViewHolder, position: Int) {
        holder.bind(wifiList[position], context)
        holder.binding.ivMore.setOnClickListener {
            mOnClickListener?.onMoreWifi(wifiList[position], position)
        }
        holder.itemView.setOnClickListener {
            mOnClickListener?.onClickWifi(wifiList[position], position)
        }
    }

    override fun getItemCount(): Int = wifiList.size

    fun deleteItem(data: ProtectWifiData, position: Int) {
        wifiList.remove(data)
        notifyItemRemoved(position)
    }
}

interface OnClickWifi {
    fun onClickWifi(data: ProtectWifiData, position: Int)

    fun onMoreWifi(data: ProtectWifiData, position: Int)
}