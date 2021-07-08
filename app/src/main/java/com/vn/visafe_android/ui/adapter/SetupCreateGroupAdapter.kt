package com.vn.visafe_android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemSetupCreateGroupBinding
import com.vn.visafe_android.ui.create.group.SetupCreateGroup
import com.vn.visafe_android.utils.setOnSingClickListener

class SetupCreateGroupAdapter(val context: Context): RecyclerView.Adapter<SetupCreateGroupAdapter.SetupViewHolder>() {
    val dataList = SetupCreateGroup.values()
    var onClickSetupGroup: OnClickSetupGroup? = null

    class SetupViewHolder private constructor(val binding: ItemSetupCreateGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SetupCreateGroup, context: Context) {
            binding.ivSetup.setImageResource(item.image)
            binding.tvTitle.text = context.getString(item.title)
            binding.tvContent.text = context.getString(item.content)
            binding.tvAdvance.visibility = if (item.isHighSetup) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.viewDivider.visibility = if (item.isHighSetup) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.switchWidget.isChecked = item.isSelected
            binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked
            }

        }

        companion object {
            fun from(parent: ViewGroup): SetupViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSetupCreateGroupBinding.inflate(layoutInflater, parent, false)
                return SetupViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetupViewHolder {
        return SetupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SetupViewHolder, position: Int) {
        holder.bind(dataList[position], context)
        holder.binding.tvAdvance.setOnSingClickListener {
            onClickSetupGroup?.onClickSetupGroup(dataList[position])
        }
    }

    override fun getItemCount(): Int = dataList.size

    interface OnClickSetupGroup {
        fun onClickSetupGroup(data: SetupCreateGroup)
    }
}