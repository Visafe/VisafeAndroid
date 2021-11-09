package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemWebsitesBinding
import vn.ncsc.visafe.model.Subject

class WebsiteAdapter(private val onItemClick: (Subject) -> Unit) :
    RecyclerView.Adapter<WebsiteAdapter.ViewHolder>() {

    private val mData = ArrayList<Subject>()

    inner class ViewHolder(private val binding: ItemWebsitesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: Subject) {
            binding.tvTitle.text = data.title

            binding.switchWidget.isChecked = data.isChecked

            binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
                data.isChecked = isChecked
            }

            itemView.setOnClickListener {
                onItemClick.invoke(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemWebsitesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun deleteItem(data: Subject) {
        val positionItem = mData.indexOf(data)
        mData.remove(data)
        notifyItemRemoved(positionItem)
    }

    fun addItem(data: Subject) {
        mData.add(data)
        notifyItemInserted(mData.size)
    }


    fun editItem(oldData: Subject, newData: Subject) {
        oldData.title = newData.title
        notifyItemChanged(mData.indexOf(oldData))
    }

    fun getData(): MutableList<String>? {
        val list: MutableList<String> = mutableListOf()
        for (i in mData) {
            if (i.isChecked) {
                list.add(i.value)
            }
        }
        return list
    }
}