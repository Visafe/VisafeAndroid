package com.vn.visafe_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemContentMostBinding
import com.vn.visafe_android.model.ContentMostData

class ContentMostAdapter(val contentList: List<ContentMostData>) :
    RecyclerView.Adapter<ContentMostAdapter.ContentViewHolder>() {

    class ContentViewHolder private constructor(val binding: ItemContentMostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContentMostData) {
            binding.data = item
        }

        companion object {
            fun from(parent: ViewGroup): ContentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemContentMostBinding.inflate(layoutInflater, parent, false)
                return ContentViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(contentList[position])
    }

    override fun getItemCount(): Int = contentList.size
}