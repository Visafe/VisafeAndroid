package com.vn.visafe_android.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemGroupMenuBinding
import com.vn.visafe_android.model.WorkspaceGroupData

class MenuAdapter(private val groupList: List<WorkspaceGroupData>, private val onClickMenu: OnClickMenu) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder private constructor(val binding: ItemGroupMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceGroupData) {
            binding.data = item
        }

        companion object {
            fun from(parent: ViewGroup): MenuViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGroupMenuBinding.inflate(layoutInflater, parent, false)
                return MenuViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(groupList[position])
        holder.itemView.setOnClickListener {
            onClickMenu.onClickMenu(groupList[position], position)
        }
        holder.binding.ivMore.setOnClickListener {
            onClickMenu.onMoreGroup()
        }
    }

    override fun getItemCount(): Int = groupList.size

    fun setSelected(position: Int) {
        groupList[position].isSelected = true
        for (i in groupList) {
            if (i != groupList[position])
                i.isSelected = false
        }
        notifyDataSetChanged()
    }
}

interface OnClickMenu {
    fun onClickMenu(data: WorkspaceGroupData, position: Int)

    fun onMoreGroup()
}