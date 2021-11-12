package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemGridOtherUtilitiesBinding
import vn.ncsc.visafe.model.OtherUtilitiesModel
import vn.ncsc.visafe.ui.home.OverViewProtectFragment
import vn.ncsc.visafe.utils.setOnSingClickListener

class HandbookAdapter(
    private var listData: MutableList<OtherUtilitiesModel>,
    private var onItemClick: OverViewProtectFragment
) : RecyclerView.Adapter<HandbookAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HandbookAdapter.ViewHolder {
        val binding =
            ItemGridOtherUtilitiesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(listData[position])
        holder.itemView.setOnSingClickListener {
            onItemClick.invoke(listData[position], position)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(private val binding: ItemGridOtherUtilitiesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: OtherUtilitiesModel) {
            binding.tvTitle.text = data.title
            binding.ivHomeUtilitiesProtectName.setImageDrawable(ContextCompat.getDrawable(itemView.context, data.resIcon))
        }
    }


}