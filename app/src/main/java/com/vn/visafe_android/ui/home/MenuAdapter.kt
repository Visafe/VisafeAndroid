package com.vn.visafe_android.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemGroupMenuBinding
import com.vn.visafe_android.model.WorkspaceGroupData

class MenuAdapter(val groupList: List<WorkspaceGroupData>, val onClickMenu: OnClickMenu) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private var currentPreviosPostion = -1

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
            if (currentPreviosPostion == -1) {
                currentPreviosPostion = position
            }
            onClickMenu.onClickMenu(groupList[position], position)
        }
        holder.binding.ivMore.setOnClickListener {
            onClickMenu.onMoreGroup()
        }
    }

    override fun getItemCount(): Int = groupList.size

    fun setSelected(position: Int) {
        groupList.forEachIndexed { index, data ->
            data.isSelected = index == position
        }
        notifyItemChanged(position)
        notifyItemChanged(currentPreviosPostion)
        currentPreviosPostion = position
    }
}

interface OnClickMenu {
    fun onClickMenu(data : WorkspaceGroupData, position: Int)

    fun onMoreGroup()
}