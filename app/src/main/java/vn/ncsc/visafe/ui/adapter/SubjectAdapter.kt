package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.ncsc.visafe.databinding.ItemSubjectBinding
import vn.ncsc.visafe.model.Subject

class SubjectAdapter : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    private val mData = ArrayList<Subject>()
    private var mOnSwitchItemListener: ((CompoundButton, Boolean, Int) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: Subject) {
            println("${data.title} ${data.isChecked}")
            binding.tvTitle.text = data.title
            Glide.with(itemView.context)
                .load(data.icon)
                .into(binding.ivItem)
            binding.switchWidget.isChecked = data.isChecked

            binding.switchWidget.setOnCheckedChangeListener { buttonView, isChecked ->
                data.isChecked = isChecked
                mOnSwitchItemListener?.invoke(buttonView, isChecked, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    fun setData(data: ArrayList<Subject>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnSwitchItem(onSwitchChange: (CompoundButton, Boolean, Int) -> Unit) {
        mOnSwitchItemListener = onSwitchChange
    }

    fun getData(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()
        for (i in mData) {
            if (i.isChecked) {
                list.add(i.value)
            }
        }
        return list
    }
}