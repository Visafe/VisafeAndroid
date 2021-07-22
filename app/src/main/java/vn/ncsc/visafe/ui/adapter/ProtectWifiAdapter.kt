package vn.ncsc.visafe.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemProtectWifiBinding
import vn.ncsc.visafe.model.DetailBotnet
import vn.ncsc.visafe.utils.getTimeAgo
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProtectWifiAdapter(val wifiList: ArrayList<DetailBotnet>, val context: Context) :
    RecyclerView.Adapter<ProtectWifiAdapter.ProtectWifiViewHolder>() {
    private var mOnClickListener: OnClickWifi? = null

    fun setOnClickListener(onConfirmListener: OnClickWifi) {
        mOnClickListener = onConfirmListener
    }

    class ProtectWifiViewHolder private constructor(val binding: ItemProtectWifiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DetailBotnet, context: Context) {
            binding.tvLink.text = "${item.mw_type} - ${item.cc_ip}:${item.cc_port}"
            Glide.with(context)
                .load("https://www.google.com/s2/favicons?sz=64&domain_url=" + item.mw_type)
                .apply(
                    RequestOptions()
                        .error(R.drawable.ic_group)
                )
                .circleCrop()
                .into(binding.ivWifi)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val date = dateFormat.parse(item?.lastseen)
            binding.tvContent.text = context.getString(R.string.da_chan, getTimeAgo(date.time))
        }

        companion object {
            fun from(parent: ViewGroup): ProtectWifiViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProtectWifiBinding.inflate(layoutInflater, parent, false)
                return ProtectWifiViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProtectWifiViewHolder {
        return ProtectWifiViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProtectWifiViewHolder, position: Int) {
        holder.bind(wifiList[position], context)
        holder.binding.ivMore.setOnClickListener {
            mOnClickListener?.onMoreWifi(wifiList[position], position)
        }
        holder.itemView.setOnClickListener {
            mOnClickListener?.onClickWifi(wifiList[position], position)
        }
    }

    override fun getItemCount(): Int = wifiList.size

    fun deleteItem(data: DetailBotnet, position: Int) {
        wifiList.remove(data)
        notifyItemRemoved(position)
    }
}

interface OnClickWifi {
    fun onClickWifi(data: DetailBotnet, position: Int)

    fun onMoreWifi(data: DetailBotnet, position: Int)
}