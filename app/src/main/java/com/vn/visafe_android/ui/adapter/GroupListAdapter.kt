package com.vn.visafe_android.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemGroupBinding
import com.vn.visafe_android.model.GroupData

class GroupListAdapter(val groupList: List<GroupData>) :
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    class GroupViewHolder private constructor(val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: GroupData) {
            binding.data = item
            binding.tvContent.text = "${item.amoutMember} thành viên • ${item.amoutDevice} thiết bị"
        }

        companion object {
            fun from(parent: ViewGroup): GroupViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGroupBinding.inflate(layoutInflater, parent, false)
                return GroupViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groupList[position])
    }

    override fun getItemCount(): Int = groupList.size
}