package vn.ncsc.visafe.ui.protect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemScanDeviceBinding

class ScanDeviceAdapter(private var mData: MutableList<String>) :
    RecyclerView.Adapter<ScanDeviceAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemScanDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: String) {
            binding.tvError.text = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemScanDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(mData[position])
    }

    override fun getItemCount(): Int = mData.size

}