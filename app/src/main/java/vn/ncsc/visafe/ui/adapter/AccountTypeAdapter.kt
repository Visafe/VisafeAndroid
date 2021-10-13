package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemAccountTypeBinding
import vn.ncsc.visafe.model.WorkspaceGroupData

class AccountTypeAdapter(
    private val workspaceList: MutableList<WorkspaceGroupData>,
    private val onClickMenu: OnClickMenu
) :
    RecyclerView.Adapter<AccountTypeAdapter.AccountTypeHolder>() {
    class AccountTypeHolder private constructor(val binding: ItemAccountTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceGroupData) {
            binding.data = item
            binding.tvNameGroup.isSelected = true
        }

        companion object {
            fun from(parent: ViewGroup): AccountTypeHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAccountTypeBinding.inflate(layoutInflater, parent, false)
                return AccountTypeHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountTypeHolder {
        return AccountTypeHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AccountTypeHolder, position: Int) {
        holder.bind(workspaceList[position])
        holder.itemView.setOnClickListener {
            onClickMenu.onClickMenu(workspaceList[position], position)
        }
        holder.binding.ivMore.setOnClickListener {
            onClickMenu.onMoreGroup(workspaceList[position], position)
        }
    }

    override fun getItemCount(): Int = workspaceList.size

    fun setSelected(position: Int) {
        if (workspaceList.size > 0) {
            for (i in workspaceList.indices) {
                if (i != position)
                    workspaceList[i].isSelected = false
            }
            workspaceList[position].isSelected = true
            notifyDataSetChanged()
        }
    }

    fun deleteItem(data: WorkspaceGroupData, position: Int) {
        workspaceList.remove(data)
        notifyItemRemoved(position)
        for (i in workspaceList.indices) {
            workspaceList[i].isSelected = i == 0
        }
        notifyDataSetChanged()
    }

    fun updateName(newName: String, position: Int) {
        workspaceList[position].name = newName
        notifyDataSetChanged()
    }
}

interface OnClickMenu {
    fun onClickMenu(data: WorkspaceGroupData, position: Int)

    fun onMoreGroup(data: WorkspaceGroupData, position: Int)
}