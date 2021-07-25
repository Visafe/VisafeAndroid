package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemUpgradeBinding

class UpgradeAdapter(val strings: List<String>) :
    RecyclerView.Adapter<UpgradeAdapter.UpgradeViewHolder>() {
    class UpgradeViewHolder private constructor(val binding: ItemUpgradeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.data = item
        }

        companion object {
            fun from(parent: ViewGroup): UpgradeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUpgradeBinding.inflate(layoutInflater, parent, false)
                return UpgradeViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpgradeViewHolder {
        return UpgradeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UpgradeViewHolder, position: Int) {
        holder.bind(strings[position])
    }

    override fun getItemCount(): Int = strings.size
}