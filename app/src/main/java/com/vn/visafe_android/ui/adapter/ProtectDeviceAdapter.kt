package com.vn.visafe_android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.R
import com.vn.visafe_android.databinding.ItemProtectDeviceBinding
import com.vn.visafe_android.model.DeviceData

class ProtectDeviceAdapter(val deviceList: ArrayList<DeviceData>, val context: Context) :
    RecyclerView.Adapter<ProtectDeviceAdapter.ProtectDeviceViewHolder>() {
    private var mOnClickListener: OnClickDevice? = null

    fun setOnClickListener(onConfirmListener: OnClickDevice) {
        mOnClickListener = onConfirmListener
    }

    class ProtectDeviceViewHolder private constructor(val binding: ItemProtectDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DeviceData, context: Context) {
            binding.data = item
            binding.ivDevice.setImageResource(
                if (item.isProtected) {
                    R.drawable.ic_checkmark_circle
                } else {
                    R.drawable.ic_info_circle
                }
            )
            binding.tvContent.text = context.getString(R.string.da_chan, item.dayBlock.toString())
        }

        companion object {
            fun from(parent: ViewGroup): ProtectDeviceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProtectDeviceBinding.inflate(layoutInflater, parent, false)
                return ProtectDeviceViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProtectDeviceViewHolder {
        return ProtectDeviceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProtectDeviceViewHolder, position: Int) {
        holder.bind(deviceList[position], context)
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