package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.utils.setBackgroundTintExt
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemDaySelectorBinding
import vn.ncsc.visafe.model.DaySelector

class DaySelectorAdapter(datas: List<String>) :
    RecyclerView.Adapter<DaySelectorAdapter.ViewHolder>() {

    private val mDatas = ArrayList<DaySelector>()

    init {
        datas.forEach {
            mDatas.add(DaySelector(it))
        }
    }

    inner class ViewHolder(private val binding: ItemDaySelectorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: DaySelector) {
            binding.tvTitle.text = data.title

            with(binding.tvTitle) {
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (data.isSelected) R.color.white else R.color.black
                    )
                )
                setBackgroundTintExt(if (data.isSelected) R.color.color_FFB31F else R.color.color_D1D1D1)
            }

            itemView.setOnClickListener {
                data.isSelected = !data.isSelected
                notifyItemChanged(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDaySelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(mDatas[position])
    }

    override fun getItemCount(): Int = mDatas.size

    fun getItemSelected(): List<DaySelector> {
        return mDatas.filter {
            it.isSelected
        }
    }
}
