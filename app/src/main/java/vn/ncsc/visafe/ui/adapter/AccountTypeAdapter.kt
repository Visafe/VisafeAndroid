package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemAccountTypeBinding
import vn.ncsc.visafe.model.WorkspaceGroupData

class AccountTypeAdapter(
    private val groupList: MutableList<WorkspaceGroupData>,
    private val onClickMenu: OnClickMenu
) :
    RecyclerView.Adapter<AccountTypeAdapter.AccountTypeHolder>() {
    class AccountTypeHolder private constructor(val binding: ItemAccountTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceGroupData) {
            binding.data = item
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
        holder.bind(groupList[position])
        holder.itemView.setOnClickListener {
            onClickMenu.onClickMenu(groupList[position], position)
        }
        holder.binding.ivMore.setOnClickListener {
            onClickMenu.onMoreGroup(groupList[position], position)
        }
    }

    override fun getItemCount(): Int = groupList.size

    fun setSelected(position: Int) {
        groupList[position].isSelected = true
        for (i in groupList.indices) {
            if (i != position)
                groupList[i].isSelected = false
        }
        notifyDataSetChanged()
    }

    fun deleteItem(data: WorkspaceGroupData, position: Int) {
        groupList.remove(data)
        notifyItemRemoved(position)
        for (i in groupList.indices) {
            groupList[i].isSelected = i == 0
        }
        notifyDataSetChanged()
    }

    fun updateName(newName: String, position: Int) {
        groupList[position].name = newName
        notifyDataSetChanged()
    }
}

interface OnClickMenu {
    fun onClickMenu(data: WorkspaceGroupData, position: Int)

    fun onMoreGroup(data: WorkspaceGroupData, position: Int)
}