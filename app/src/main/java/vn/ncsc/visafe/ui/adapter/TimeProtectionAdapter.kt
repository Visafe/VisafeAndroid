package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemTimeBinding
import vn.ncsc.visafe.model.TimeProtection

class TimeProtectionAdapter(private val onClickItemListener: (TimeProtection) -> Unit) :
    RecyclerView.Adapter<TimeProtectionAdapter.ViewHolder>() {

    private val mData = ArrayList<TimeProtection>()

    inner class ViewHolder(private val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: TimeProtection) {

            if (data.isProtectionAllDay) {
                binding.tvStartTime.text = itemView.context.getString(R.string.all_day)
                binding.tvEndTime.visibility = View.GONE
                binding.viewOne.visibility = View.GONE

            } else {
                binding.tvStartTime.text = data.startTime
                binding.tvEndTime.text = data.endTime
                binding.tvEndTime.visibility = View.VISIBLE
                binding.viewOne.visibility = View.VISIBLE
            }
            binding.tvStartTime.requestLayout()
            binding.switchWidget.isChecked = data.isChecked

            binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
                data.isChecked = isChecked
            }

            itemView.setOnClickListener {
                onClickItemListener.invoke(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    fun setData(data: ArrayList<TimeProtection>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun deleteItem(data: TimeProtection) {
        val positionItem = mData.indexOf(data)
        mData.remove(data)
        notifyItemRemoved(positionItem)
    }

    fun addItem(data: TimeProtection) {
        mData.add(data)
        notifyItemInserted(mData.size)
    }


    fun editItem(oldData: TimeProtection, newData: TimeProtection) {
        oldData.isProtectionAllDay = newData.isProtectionAllDay
        oldData.startTime = newData.startTime
        oldData.endTime = newData.endTime

        notifyItemChanged(mData.indexOf(oldData))
    }
}