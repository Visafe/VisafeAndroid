package vn.ncsc.visafe.ui.group.detail.device

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_member.view.*
import vn.ncsc.visafe.R
import vn.ncsc.visafe.model.response.DeviceGroup
import vn.ncsc.visafe.utils.setOnSingClickListener

class DeviceManagerAdapter(private var onClickDevice: OnClickDevice) :
    ListAdapter<DeviceGroup, DeviceManagerAdapter.MyViewHolder>(Comparator()) {
    private var deviceList: MutableList<DeviceGroup> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: DeviceGroup = getItem(position)
        item?.let {
            holder.bindView(item)
            holder.itemView.setOnSingClickListener {
                onClickDevice.onClickDevice(item, position)
            }
            holder.itemView.ivMore.setOnSingClickListener {
                onClickDevice.onMoreDevice(item, position)
            }
        }
    }

    fun setData(list: MutableList<DeviceGroup>?) {
        list?.let {
            this.deviceList = it
        }
        submitList(list)
    }

    fun deleteItem(data: DeviceGroup) {
        val positionItem = deviceList.indexOf(data)
        deviceList.remove(data)
        notifyItemRemoved(positionItem)
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.item_device, parent, false
        )
    ) {
        fun bindView(item: DeviceGroup) {
            itemView.tvName.text = item.deviceName
            itemView.tvContent.text = item.deviceOwner
        }
    }

    interface OnClickDevice {
        fun onClickDevice(data: DeviceGroup, position: Int)
        fun onMoreDevice(data: DeviceGroup, position: Int)
    }

    class Comparator : DiffUtil.ItemCallback<DeviceGroup>() {
        override fun areItemsTheSame(oldItem: DeviceGroup, newItem: DeviceGroup): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DeviceGroup, newItem: DeviceGroup): Boolean {
            return oldItem == newItem
        }
    }
}