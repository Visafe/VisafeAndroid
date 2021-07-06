package com.vn.visafe_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemGroupDashboardBinding
import com.vn.visafe_android.model.GroupPeopleData

class GroupDashboardAdapter(val groupdashboardList: List<GroupPeopleData>) :
    RecyclerView.Adapter<GroupDashboardAdapter.GroupDashboardViewHolder>() {

    class GroupDashboardViewHolder private constructor(val binding: ItemGroupDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupPeopleData) {
            binding.data = item
            val adapterNoti = GroupPeopleNotiAdapter(item.groupPeopleNotiDataList!!)
            binding.rvGroupPeopleNoti.adapter = adapterNoti
        }

        companion object {
            fun from(parent: ViewGroup): GroupDashboardViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGroupDashboardBinding.inflate(layoutInflater, parent, false)
                return GroupDashboardViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupDashboardViewHolder {
        return GroupDashboardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GroupDashboardViewHolder, position: Int) {
        holder.bind(groupdashboardList[position])
    }

    override fun getItemCount(): Int = groupdashboardList.size
}