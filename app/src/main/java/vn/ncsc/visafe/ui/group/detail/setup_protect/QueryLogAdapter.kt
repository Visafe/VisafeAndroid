package vn.ncsc.visafe.ui.group.detail.setup_protect

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemProtectWifiBinding
import vn.ncsc.visafe.model.QueryLogData
import vn.ncsc.visafe.utils.getTimeAgo
import java.text.SimpleDateFormat
import java.util.*

class QueryLogAdapter(private val queryLogList: MutableList<QueryLogData>, val context: Context) :
    RecyclerView.Adapter<QueryLogAdapter.QueryLogViewHolder>() {
    private var mOnClickMoreItemQuery: OnClickMoreItemQuery? = null

    fun setOnClickListener(onClickMoreItemQuery: OnClickMoreItemQuery) {
        mOnClickMoreItemQuery = onClickMoreItemQuery
    }

    class QueryLogViewHolder private constructor(val binding: ItemProtectWifiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QueryLogData, context: Context) {
            binding.tvLink.text = item.question?.host
            Glide.with(context)
                .load("https://www.google.com/s2/favicons?sz=64&domain_url=" + item.question?.host)
                .apply(
                    RequestOptions()
                        .error(R.drawable.ic_group)
                )
                .circleCrop()
                .into(binding.ivWifi)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = dateFormat.parse(item.time)
            binding.tvContent.text = context.getString(R.string.da_chan, getTimeAgo(date.time))
        }

        companion object {
            fun from(parent: ViewGroup): QueryLogViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProtectWifiBinding.inflate(layoutInflater, parent, false)
                return QueryLogViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryLogViewHolder {
        return QueryLogViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: QueryLogViewHolder, position: Int) {
        holder.bind(queryLogList[position], context)
        holder.binding.ivMore.setOnClickListener {
            mOnClickMoreItemQuery?.onClickMore(queryLogList[position], position)
        }
    }

    override fun getItemCount(): Int = queryLogList.size

    fun deleteItem(data: QueryLogData, position: Int) {
        queryLogList.remove(data)
        notifyItemRemoved(position)
    }
}

interface OnClickMoreItemQuery {
    fun onClickMore(data: QueryLogData, position: Int)
}