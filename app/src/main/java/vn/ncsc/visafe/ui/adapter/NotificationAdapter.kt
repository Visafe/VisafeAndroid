package vn.ncsc.visafe.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_notification.view.*
import vn.ncsc.visafe.R
import vn.ncsc.visafe.model.NotificationModel
import vn.ncsc.visafe.utils.getTimeAgo

class NotificationAdapter(private val onSelectItemListener: OnSelectItemListener) :
    ListAdapter<NotificationModel, NotificationAdapter.MyViewHolder>(Comparator()) {
    private var notificationList: MutableList<NotificationModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: NotificationModel = getItem(position)
        item?.let {
            holder.bindView(item)
            holder.itemView.setOnClickListener {
                onSelectItemListener.onSelectItem(item)
            }
        }
    }

    fun setData(list: MutableList<NotificationModel>?) {
        list?.let {
            this.notificationList = it
        }
        submitList(list)
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.item_notification, parent, false
        )
    ) {
        @SuppressLint("SetTextI18n")
        fun bindView(item: NotificationModel?) {
            var title = ""
            when (item?.content?.type) {
                "ALERT_DOMAIN" -> {
                    title =
                        "Thiết bị ${item.content?.affected?.name} đã cố gắng truy cập trang web: ${item.content?.target?.domain}"
                }
                "INVITE_SUCCESS" -> {
                    title = "${item.content?.affected?.name} đã là thành viên của nhóm ${item.group?.name}"
                }
                "JOIN_SUCCESS" -> {
                    title = "Bạn đã là thành viên của nhóm ${item.group?.name}"
                }
                "DEVICE_JOIN_SUCCESS" -> {
                    title = "Thiết bị ${item.content?.affected?.name} vừa được thêm vào nhóm ${item.group?.name}"
                }
                "ALERT_TRANSACTION" -> {
                    if ("0" == item.content?.status_payment) {
                        title =
                            "Bạn đã giao dịch thành công gói ${item.content?.package_name} trong thời gian ${item.content?.duration} tháng"
                    } else {
                        title =
                            "Giao dịch thất bại gói ${item.content?.package_name} trong thời gian ${item.content?.duration} tháng"
                    }
                }
            }
            itemView.tvTitle.text = title + "\uD83D\uDD25"
            itemView.tvTime.text = item?.createdAt?.let { getTimeAgo(it.toLong()) }
            if (item?.isRead == true)
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.color_FFF9ED))
        }
    }

    interface OnSelectItemListener {
        fun onSelectItem(item: NotificationModel)
    }

    class Comparator : DiffUtil.ItemCallback<NotificationModel>() {
        override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return !(oldItem.isRead != newItem.isRead
                    || oldItem.isSee != newItem.isSee
                    || oldItem.id != newItem.id)
        }

        override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem == newItem
        }
    }
}

enum class TypeNotification(
    val type: String,
    val titleNoti: String,
    val resDrawableIcon: Int,
) {
    ALERT_DOMAIN(
        "ALERT_DOMAIN",
        "Con người",
        R.drawable.ic_notify_protect_device
    ),
    INVITE_SUCCESS(
        "INVITE_SUCCESS",
        "Gia đình & nhóm",
        R.drawable.bg_top_protect_family_group
    ),
    JOIN_SUCCESS(
        "JOIN_SUCCESS",
        "Bảo vệ tổ chức",
        R.drawable.bg_top_protect_enterprise_group
    ),
    DEVICE_JOIN_SUCCESS(
        "DEVICE_JOIN_SUCCESS",
        "Gia đình & nhóm",
        R.drawable.bg_top_protect_family_group
    );

    companion object {
        private val mapType = values().associateBy(TypeNotification::type)
        fun fromIsTypeWorkSpaces(type: String?) = mapType[type]
    }
}