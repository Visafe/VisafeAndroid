package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemTimeStatisticalBinding

class TimeStatisticalAdapter(
    private val onClickItem: OnClickItem
) : RecyclerView.Adapter<TimeStatisticalAdapter.AccountTypeHolder>() {

    private val listItem = TimeStatistical.values()

    class AccountTypeHolder private constructor(val binding: ItemTimeStatisticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeStatistical) {
            binding.tvValue.text = item.time
            binding.root.setBackgroundResource(if (item.selected) R.drawable.bg_menu_group else 0)
        }

        companion object {
            fun from(parent: ViewGroup): AccountTypeHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTimeStatisticalBinding.inflate(layoutInflater, parent, false)
                return AccountTypeHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountTypeHolder {
        return AccountTypeHolder.from(parent)
    }

    fun setSelected(position: Int) {
        for (i in listItem.indices) {
            if (i != position)
                listItem[i].selected = false
        }
        listItem[position].selected = true
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AccountTypeHolder, position: Int) {
        listItem[position]?.let { item ->
            holder.bind(item)
            holder.itemView.setOnClickListener {
                onClickItem.onClickItem(item, position)
            }
        }
    }

    override fun getItemCount(): Int = listItem.size
}

interface OnClickItem {
    fun onClickItem(item: TimeStatistical, position: Int)
}

enum class TimeStatistical(
    val time: String,
    val value: String,
    var selected: Boolean
) {
    HANG_NGAY(
        "Trong ngày",
        "24",
        true
    ),
    HANG_TUAN(
        "Trong tuần",
        "168",
        false
    ),
    HANG_THANG(
        "Trong tháng",
        "744",
        false
    )
}