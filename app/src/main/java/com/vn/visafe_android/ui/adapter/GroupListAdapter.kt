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
        const val TYPE_CREATE_GROUP = 1
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

    class CreateGroupViewHolder private constructor(val binding: ItemCreateGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): CreateGroupViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCreateGroupBinding.inflate(layoutInflater, parent, false)
                return CreateGroupViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_GROUP -> GroupViewHolder.from(parent)
            TYPE_CREATE_GROUP -> CreateGroupViewHolder.from(parent)
            else -> GroupViewHolder.from(parent)
        }
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
        } else {
            (holder as CreateGroupViewHolder).itemView.setOnSingClickListener {
                onClickGroup?.createGroup()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (groupList[position] != null) {
            TYPE_GROUP
        } else {
            TYPE_CREATE_GROUP
        }
    }

    override fun getItemCount(): Int = groupList.size

    interface OnClickGroup {
        fun openGroup(data: GroupData)
        fun createGroup()
        fun onClickMore()
    }
}