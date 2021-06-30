package com.vn.visafe_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vn.visafe_android.databinding.ItemConfigBinding
import com.vn.visafe_android.utils.SetupConfig

class ConfigAdapter : RecyclerView.Adapter<ConfigAdapter.ConfigViewHolder>() {
    private val list = SetupConfig.values()
    var onChangeConfig: OnChangeConfig? = null

    class ConfigViewHolder private constructor(val binding: ItemConfigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SetupConfig, onChangeConfig: OnChangeConfig?) {
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
            binding.ivConfig.setImageResource(item.image)
            binding.switchType.isChecked = item.selected
            binding.switchType.setOnCheckedChangeListener { buttonView, isChecked ->
                item.selected = isChecked
                binding.switchType.isChecked = isChecked
                onChangeConfig?.onChangeConfig(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ConfigViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemConfigBinding.inflate(layoutInflater, parent, false)
                return ConfigViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigViewHolder {
        return ConfigViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ConfigViewHolder, position: Int) {
        holder.bind(list[position], onChangeConfig)
    }

    override fun getItemCount(): Int = list.size

    interface OnChangeConfig {
        fun onChangeConfig(data: SetupConfig)
    }
}