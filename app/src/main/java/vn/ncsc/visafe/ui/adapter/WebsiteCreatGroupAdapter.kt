package vn.ncsc.visafe.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.databinding.ItemWebsiteCreateGroupBinding
import vn.ncsc.visafe.model.Subject

class WebsiteCreatGroupAdapter(private val onItemClick: (Subject) -> Unit) :
    RecyclerView.Adapter<WebsiteCreatGroupAdapter.WebsiteViewHolder>() {
    private val mData = ArrayList<Subject>()

    inner class WebsiteViewHolder(private val binding: ItemWebsiteCreateGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: Subject) {
            binding.cbLink.text = data.title
            binding.cbLink.isChecked = data.isChecked

            binding.cbLink.setOnCheckedChangeListener { _, isChecked ->
                data.isChecked = isChecked
            }

            binding.ivMore.setOnClickListener {
                onItemClick.invoke(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebsiteViewHolder {
        val binding =
            ItemWebsiteCreateGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WebsiteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WebsiteViewHolder, position: Int) {
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