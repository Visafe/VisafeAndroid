package vn.ncsc.visafe.ui.group.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.ItemSetupCreateGroupBinding
import vn.ncsc.visafe.utils.setOnSingClickListener

class SetupProtectGroupDetailAdapter(
    val context: Context,
    var onClickSetupGroup: OnClickSetupGroup?
) :
    RecyclerView.Adapter<SetupProtectGroupDetailAdapter.SetupViewHolder>() {
    val dataList = SetupProtectDetailGroup.values()

    class SetupViewHolder private constructor(val binding: ItemSetupCreateGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SetupProtectDetailGroup, context: Context) {
            binding.ivSetup.setImageResource(item.image)
            binding.tvTitle.text = context.getString(item.title)
            binding.tvContent.text = context.getString(item.content)
            binding.tvAdvance.text = item.advance
            binding.tvAdvance.visibility = if (item.isHighSetup) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.viewDivider.visibility = if (item.isHighSetup) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.switchWidget.visibility = if (item.isShowSwitch) View.VISIBLE else View.GONE
            binding.switchWidget.isChecked = item.isEnable
        }

        companion object {
            fun from(parent: ViewGroup): SetupViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSetupCreateGroupBinding.inflate(layoutInflater, parent, false)
                return SetupViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetupViewHolder {
        return SetupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SetupViewHolder, position: Int) {
        holder.bind(dataList[position], context)
        holder.binding.tvAdvance.setOnSingClickListener {
            onClickSetupGroup?.onClickSetupGroup(dataList[position])
        }
        holder.binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
            dataList[position].isEnable = isChecked
            onClickSetupGroup?.onSwitchItemChange(dataList[position],isChecked)
        }
    }

    override fun getItemCount(): Int = dataList.size

    interface OnClickSetupGroup {
        fun onClickSetupGroup(data: SetupProtectDetailGroup)

        fun onSwitchItemChange(data: SetupProtectDetailGroup, isChecked: Boolean)
    }
}

enum class SetupProtectDetailGroup(
    var image: Int,
    var title: Int,
    var content: Int,
    var advance: String,
    var isEnable: Boolean,
    var isHighSetup: Boolean,
    var isShowSwitch: Boolean
) {
    BAO_VE_THIET_BI(
        R.drawable.ic_device_manager,
        R.string.protect_device,
        R.string.content_protect_device,
        "Xem chi tiết",
        true,
        true,
        false
    ),
    CHAN_QUANG_CAO(
        R.drawable.ic_chan_quang_cao,
        R.string.chan_quang_cao,
        R.string.chan_quang_cao_content,
        "Xem chi tiết",
        true,
        true,
        true
    ),
    CHAN_THEO_DOI(
        R.drawable.ic_chan_theo_doi,
        R.string.chan_theo_doi,
        R.string.chan_theo_doi_content,
        "Xem chi tiết",
        true,
        true,
        true
    ),
    CHAN_TRUY_CAP(
        R.drawable.ic_chan_truy_cap,
        R.string.chan_truy_cap,
        R.string.chan_truy_cap_content,
        "Xem chi tiết",
        true,
        true,
        true
    ),
    CHAN_NOI_DUNG(
        R.drawable.ic_chan_noi_dung,
        R.string.chan_noi_dung,
        R.string.chan_noi_dung_content,
        "Thiết lập nâng cao",
        true,
        true,
        true
    ),
    CHAN_VPN_PROXY(
        R.drawable.ic_chan_vpn,
        R.string.chan_vpn,
        R.string.chan_vpn_content,
        "",
        true,
        false,
        true
    )
}