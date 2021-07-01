package com.vn.visafe_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vn.visafe_android.databinding.ItemSubjectBinding
import com.vn.visafe_android.model.Subject

class SubjectAdapter : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    private val mData = ArrayList<Subject>()

    inner class ViewHolder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: Subject) {
            println("${data.title} ${data.isChecked}")
            binding.tvTitle.text = data.title
            Glide.with(itemView.context)
                .load(data.icon)
                .into(binding.ivItem)
            binding.switchWidget.isChecked = data.isChecked

            binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
                data.isChecked = isChecked
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
        mData.addAll(data)
        notifyDataSetChanged()
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