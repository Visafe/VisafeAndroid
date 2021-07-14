package vn.ncsc.visafe.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemBlockTrackingBinding
import vn.ncsc.visafe.model.BlockTrackingData

class BlockTrackingAdapter(val dataList: ArrayList<BlockTrackingData>, val context: Context) :
    RecyclerView.Adapter<BlockTrackingAdapter.BlockTrackingViewHolder>() {
    private var mOnClickListener: OnClickBlockTracking? = null

    fun setOnClickListener(onConfirmListener: OnClickBlockTracking) {
        mOnClickListener = onConfirmListener
    }

    class BlockTrackingViewHolder private constructor(val binding: ItemBlockTrackingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BlockTrackingData, context: Context) {
            binding.data = item
            binding.ivBlockTracking.setImageResource(
                if (item.isProtected) {
                    R.drawable.ic_checkmark_circle
                } else {
                    R.drawable.ic_info_circle
                }
            )
            binding.tvContent.text = context.getString(R.string.da_chan, item.day.toString())
        }

        companion object {
            fun from(parent: ViewGroup): BlockTrackingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBlockTrackingBinding.inflate(layoutInflater, parent, false)
                return BlockTrackingViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockTrackingViewHolder {
        return BlockTrackingViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BlockTrackingViewHolder, position: Int) {
        holder.bind(dataList[position], context)
        holder.binding.ivMore.setOnClickListener {
            mOnClickListener?.onMoreBlockTracking(dataList[position], position)
        }
        holder.itemView.setOnClickListener {
            mOnClickListener?.onClickBlockTracking(dataList[position], position)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun deleteItem(data: BlockTrackingData, position: Int) {
        dataList.remove(data)
        notifyItemRemoved(position)
    }
}

interface OnClickBlockTracking {
    fun onClickBlockTracking(data: BlockTrackingData, position: Int)

    fun onMoreBlockTracking(data: BlockTrackingData, position: Int)
}