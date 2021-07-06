package com.vn.visafe_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemDeviceBinding
import com.vn.visafe_android.model.DeviceData

class DeviceAdapter(val deviceList: ArrayList<DeviceData>) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private var mOnClickListener: OnClickDevice? = null

    fun setOnClickListener(onConfirmListener: OnClickDevice) {
        mOnClickListener = onConfirmListener
    }

    class DeviceViewHolder private constructor(val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DeviceData) {
            binding.data = item
        }

        companion object {
            fun from(parent: ViewGroup): DeviceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDeviceBinding.inflate(layoutInflater, parent, false)
                return DeviceViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(deviceList[position])
        holder.binding.ivMore.setOnClickListener {
            mOnClickListener?.onMoreDevice(deviceList[position], position)
        }
        holder.itemView.setOnClickListener {
            mOnClickListener?.onClickDevice(deviceList[position], position)
        }
    }

    override fun getItemCount(): Int = deviceList.size

    fun deleteItem(data: DeviceData, position: Int) {
        deviceList.remove(data)
        notifyItemRemoved(position)
    }
}

interface OnClickDevice {
    fun onClickDevice(data: DeviceData, position: Int)

    fun onMoreDevice(data: DeviceData, position: Int)
}