package vn.ncsc.visafe.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.utils.getTextGroup
import vn.ncsc.visafe.utils.setOnSingClickListener
import vn.ncsc.visafe.databinding.ItemGroupBinding
import vn.ncsc.visafe.model.GroupData

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
        fun bind(position: Int, item: GroupData, enableImageGroup: Boolean) {
            binding.ivGroup.visibility = if (enableImageGroup) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.tvName.text = item.name
            binding.ivGroup.text = getTextGroup(item.name)
            binding.tvContent.text = "${item.listUsersGroupInfo?.size} thành viên • ${item.listDevicesGroupInfo?.size} thiết bị"
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
            groupList[position]?.let { group ->
                (holder as GroupViewHolder).bind(position, group, enableImageGroup)
                holder.itemView.setOnSingClickListener {
                    onClickGroup?.openGroup(group, position)
                }
                holder.binding.ivMore.setOnClickListener {
                    onClickGroup?.onClickMore()
                }
            }
        }
    }

    override fun getItemCount(): Int = groupList.size

    interface OnClickGroup {
        fun openGroup(data: GroupData, position: Int)
        fun onClickMore()
    }
}