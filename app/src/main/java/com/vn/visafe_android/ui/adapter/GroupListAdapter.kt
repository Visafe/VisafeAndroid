package com.vn.visafe_android.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vn.visafe_android.R
import com.vn.visafe_android.databinding.ItemCreateGroupBinding
import com.vn.visafe_android.databinding.ItemGroupBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.utils.setOnSingClickListener

class GroupListAdapter(val groupList: List<GroupData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onClickGroup: OnClickGroup? = null
    private var enableImageGroup = false

    companion object {
        const val TYPE_GROUP = 0
    }

    fun setEnableImageGroup(enableImageGroup: Boolean) {
        this.enableImageGroup = enableImageGroup
        notifyDataSetChanged()
    }

    class GroupViewHolder private constructor(val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: GroupData, enableImageGroup: Boolean) {
            binding.data = item
            binding.ivGroup.visibility = if (enableImageGroup) {
                View.VISIBLE
            } else {
                View.GONE
            }

            Glide.with(itemView.context)
                .load(item.image)
                .apply(RequestOptions.errorOf(R.drawable.ic_group))
                .into(binding.ivGroup)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GroupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_GROUP) {
            (holder as GroupViewHolder).bind(groupList[position]!!, enableImageGroup)
            holder.itemView.setOnSingClickListener {
                onClickGroup?.openGroup(groupList[position]!!)
            }
            holder.binding.ivMore.setOnClickListener {
                onClickGroup?.onClickMore()
            }
        }
    }

    override fun getItemCount(): Int = groupList.size

    interface OnClickGroup {
        fun openGroup(data: GroupData)
        fun onClickMore()
    }
}