package com.vn.visafe_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemDeviceMostBinding
import com.vn.visafe_android.model.DeviceMostData

class DeviceMostAdapter(val deviceList: List<DeviceMostData>) :
    RecyclerView.Adapter<DeviceMostAdapter.DeviceViewHolder>() {
    class DeviceViewHolder private constructor(val binding: ItemDeviceMostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DeviceMostData) {
            binding.data = item
        }

        companion object {
            fun from(parent: ViewGroup): DeviceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeviceMostBinding.inflate(layoutInflater, parent, false)
                return DeviceViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(deviceList[position])
    }

    override fun getItemCount(): Int = deviceList.size
}