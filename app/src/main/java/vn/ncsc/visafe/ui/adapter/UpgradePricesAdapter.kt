package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_upgrade_prices.view.*
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemUpgradePricesBinding
import vn.ncsc.visafe.model.response.PriceAllPackage
import vn.ncsc.visafe.utils.formatPrices
import vn.ncsc.visafe.utils.setOnSingClickListener

class UpgradePricesAdapter(private val listPrices: MutableList<PriceAllPackage>, private var onClickPay: OnClickPay) :
    RecyclerView.Adapter<UpgradePricesAdapter.UpgradeViewHolder>() {
    class UpgradeViewHolder private constructor(val binding: ItemUpgradePricesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PriceAllPackage, position: Int) {
            itemView.tvSave.visibility = if (position == 0) View.VISIBLE else View.GONE
            itemView.tvDuration.text = itemView.context.resources.getString(R.string.goi_nam, item.duration)
            itemView.tvDayTrial.text = itemView.context.resources.getString(R.string.try_month_day, item.day_trail)
            if (item.price?.toInt() == -1) {
                itemView.tvPrice.text = itemView.context.resources.getString(R.string.price_business)
            } else {
                if (item.duration == 12) {
                    itemView.tvPrice.text = itemView.context.resources.getString(R.string.price_year,
                        item.price?.let { formatPrices(it) })
                } else {
                    itemView.tvPrice.text = itemView.context.resources.getString(R.string.price_month,
                        item.price?.let { formatPrices(it) })
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): UpgradeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUpgradePricesBinding.inflate(layoutInflater, parent, false)
                return UpgradeViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpgradeViewHolder {
        return UpgradeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UpgradeViewHolder, position: Int) {
        holder.bind(listPrices[position], position)
        holder.itemView.setOnSingClickListener {
            onClickPay.onPay(listPrices[position], position)
        }
    }

    override fun getItemCount(): Int = listPrices.size

    interface OnClickPay {
        fun onPay(item: PriceAllPackage, position: Int)
    }
}