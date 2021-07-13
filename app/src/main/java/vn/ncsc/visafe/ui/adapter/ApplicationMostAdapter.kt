package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemAppMostBinding
import vn.ncsc.visafe.model.ApplicationMostData

class ApplicationMostAdapter(val appList: List<ApplicationMostData>) :
    RecyclerView.Adapter<ApplicationMostAdapter.AppViewHolder>() {
    class AppViewHolder private constructor(val binding: ItemAppMostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ApplicationMostData) {
            binding.data = item
        }

        companion object {
            fun from(parent: ViewGroup): AppViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAppMostBinding.inflate(layoutInflater, parent, false)
                return AppViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    override fun getItemCount(): Int = appList.size
}