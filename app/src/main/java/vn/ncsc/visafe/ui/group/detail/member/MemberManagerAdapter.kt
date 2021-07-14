package vn.ncsc.visafe.ui.group.detail.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_member.view.*
import vn.ncsc.visafe.R
import vn.ncsc.visafe.model.UsersGroupInfo
import vn.ncsc.visafe.utils.setOnSingClickListener

class MemberManagerAdapter(var onSelectItemListener: OnSelectItemListener) :
    ListAdapter<UsersGroupInfo, MemberManagerAdapter.MyViewHolder>(Comparator()) {
    private var memberList: MutableList<UsersGroupInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: UsersGroupInfo = getItem(position)
        item?.let {
            holder.bindView(item)
            holder.itemView.setOnClickListener {
                onSelectItemListener.onSelectItem(item, position)
            }
            holder.itemView.ivMore.setOnSingClickListener {
                onSelectItemListener.onMore(item, position)
            }
        }
    }

    fun setData(list: MutableList<UsersGroupInfo>?) {
        list?.let {
            this.memberList = it
        }
        submitList(list)
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.item_member, parent, false
        )
    ) {
        fun bindView(item: UsersGroupInfo) {
            itemView.tvName.text = item.fullName
            itemView.tvContent.text = item.email
        }
    }

    interface OnSelectItemListener {
        fun onSelectItem(item: UsersGroupInfo, position: Int)
        fun onMore(item: UsersGroupInfo, position: Int)
    }

    class Comparator : DiffUtil.ItemCallback<UsersGroupInfo>() {
        override fun areItemsTheSame(oldItem: UsersGroupInfo, newItem: UsersGroupInfo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: UsersGroupInfo, newItem: UsersGroupInfo): Boolean {
            return oldItem == newItem
        }
    }
}