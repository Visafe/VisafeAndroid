package com.vn.visafe_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemGroupPeopleNotiBinding
import com.vn.visafe_android.model.GroupPeopleNotiData

class GroupPeopleNotiAdapter(val dataList: List<GroupPeopleNotiData>) :
    RecyclerView.Adapter<GroupPeopleNotiAdapter.GroupPeopleNotiViewHolder>() {
    class GroupPeopleNotiViewHolder private constructor(val binding: ItemGroupPeopleNotiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupPeopleNotiData) {
            binding.data = item
        }

        companion object {
            fun from(parent: ViewGroup): GroupPeopleNotiViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGroupPeopleNotiBinding.inflate(layoutInflater, parent, false)
                return GroupPeopleNotiViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupPeopleNotiViewHolder {
        return GroupPeopleNotiViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GroupPeopleNotiViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size
}