package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.utils.getTimeAgo
import kotlinx.android.synthetic.main.item_notification.view.*
import vn.ncsc.visafe.R
import vn.ncsc.visafe.model.NotificationModel

class NotificationAdapter(private val onSelectItemListener: OnSelectItemListener) :
    ListAdapter<NotificationModel, NotificationAdapter.MyViewHolder>(Comparator()),
    Filterable {
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
        fun bindView(item: NotificationModel) {
            when (item.content?.type) {
                "ALERT_DOMAIN" -> {

                }
                "INVITE_SUCCESS" -> {

                }
                "DEVICE_JOIN_SUCCESS" -> {

                }
            }
            itemView.tvTitle.text = item.content?.affected?.name
            itemView.tvTime.text = item.createdAt?.let { getTimeAgo(it.toLong()) }
            if (item.isRead == true)
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.color_FFF9ED))
        }
    }

    override fun getFilter(): Filter {
        return customFilter
    }

    private val customFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<NotificationModel>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(notificationList)
            } else {
//                for (item in notificationList) {
//                    if ((item.?.lowercase(Locale.ROOT)?.contains(constraint.toString().lowercase(Locale.ROOT))) == true
//                    ) {
//                        filteredList.add(item)
//                    }
//                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            submitList(filterResults?.values as MutableList<NotificationModel>?)
        }

    }

    interface OnSelectItemListener {
        fun onSelectItem(item: NotificationModel)
    }

    class Comparator : DiffUtil.ItemCallback<NotificationModel>() {
        override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem == newItem
        }
    }
}