package vn.ncsc.visafe.ui.group.detail.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_member.view.*
import vn.ncsc.visafe.R
import vn.ncsc.visafe.model.TimeProtection
import vn.ncsc.visafe.model.UsersGroupInfo
import vn.ncsc.visafe.utils.setBackgroundTint
import vn.ncsc.visafe.utils.setOnSingClickListener

class MemberManagerAdapter(
    private var fkUserId: Int?,
    private var listUserManage: MutableList<String>?,
    private var listUsersActive: MutableList<String>?,
    private var onSelectItemListener: OnSelectItemListener
) :
    ListAdapter<UsersGroupInfo, MemberManagerAdapter.MyViewHolder>(Comparator()) {
    private var memberList: MutableList<UsersGroupInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: UsersGroupInfo = getItem(position)
        item?.let {
            holder.bindView(fkUserId, listUserManage, listUsersActive, item)
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

    fun deleteItem(data: UsersGroupInfo) {
        val positionItem = memberList.indexOf(data)
        memberList.remove(data)
        notifyItemRemoved(positionItem)
    }

    inner class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.item_member, parent, false
        )
    ) {
        fun bindView(
            fkUserId: Int?, listUserManage: MutableList<String>?, listUsersActive: MutableList<String>?, item: UsersGroupInfo
        ) {
            itemView.tvName.text = item.fullName
            if (fkUserId.toString() == item.userID) {
                itemView.tvLevel.text = "Chủ nhóm"
                itemView.tvLevel.setBackgroundResource(R.drawable.bg_radius_4dp)
                itemView.tvLevel.setBackgroundTint(R.color.color_F9F1E2)
                itemView.tvLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_FFB31F))
                item.typePosition = TypePosition.IS_OWNER
            }
            if (listUserManage?.isNotEmpty() == true && listUserManage.isNotEmpty()) {
                for (i in listUserManage) {
                    if (i == item.userID) {
                        itemView.tvLevel.text = "Quản trị viên"
                        itemView.tvLevel.setBackgroundResource(R.drawable.bg_radius_4dp)
                        itemView.tvLevel.setBackgroundTint(R.color.color_F9F1E2)
                        itemView.tvLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_FFB31F))
                        item.typePosition = TypePosition.ADMINISTRATORS
                    }
                }
            }
            if (listUsersActive?.isNotEmpty() == true && listUsersActive.isNotEmpty()) {
                for (i in listUsersActive) {
                    if (i == item.userID) {
                        itemView.tvLevel.text = "Giám sát viên"
                        itemView.tvLevel.setBackgroundResource(R.drawable.bg_radius_4dp)
                        itemView.tvLevel.setBackgroundTint(R.color.color_ecf7ff)
                        itemView.tvLevel.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_15A1FA))
                        item.typePosition = TypePosition.SUPERVISOR
                    }
                }
            }
            itemView.tvContent.text =
                "Được phép chỉnh sửa cấu hình,..."/*if (item.email.isNullOrEmpty()) "Chưa có email" else item.email*/
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

enum class TypePosition {
    IS_OWNER,
    ADMINISTRATORS,
    SUPERVISOR
}