package vn.ncsc.visafe.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemBlockAdsBinding
import vn.ncsc.visafe.model.BlockAdsData

class BlockAdsAdapter(val dataList: ArrayList<BlockAdsData>, val context: Context) :
    RecyclerView.Adapter<BlockAdsAdapter.BlockAdsViewHolder>() {
    private var mOnClickListener: OnClickBlockAds? = null

    fun setOnClickListener(onConfirmListener: OnClickBlockAds) {
        mOnClickListener = onConfirmListener
    }

    class BlockAdsViewHolder private constructor(val binding: ItemBlockAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BlockAdsData, context: Context) {
            binding.data = item
            binding.ivBlockAds.setImageResource(
                if (item.isProtected) {
                    R.drawable.ic_checkmark_circle
                } else {
                    R.drawable.ic_info_circle
                }
            )
            binding.tvContent.text = context.getString(R.string.da_chan, item.day.toString())
        }

        companion object {
            fun from(parent: ViewGroup): BlockAdsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBlockAdsBinding.inflate(layoutInflater, parent, false)
                return BlockAdsViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockAdsViewHolder {
        return BlockAdsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BlockAdsViewHolder, position: Int) {
        holder.bind(dataList[position], context)
        holder.binding.ivMore.setOnClickListener {
            mOnClickListener?.onMoreBlockAds(dataList[position], position)
        }
        holder.itemView.setOnClickListener {
            mOnClickListener?.onClickBlockAds(dataList[position], position)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun deleteItem(data: BlockAdsData, position: Int) {
        dataList.remove(data)
        notifyItemRemoved(position)
    }
}

interface OnClickBlockAds {
    fun onClickBlockAds(data: BlockAdsData, position: Int)

    fun onMoreBlockAds(data: BlockAdsData, position: Int)
}