package vn.ncsc.visafe.ui.dialog

import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseDialogBottomSheet
import vn.ncsc.visafe.databinding.LayoutDisplayStatisticalForTimeBottomSheetBinding
import vn.ncsc.visafe.ui.adapter.OnClickItem
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.adapter.TimeStatisticalAdapter
import vn.ncsc.visafe.utils.setOnSingClickListener

class DisplayStatisticalForTimeBottomSheet(private var onClickItemTime: OnClickItemTime) :
    BaseDialogBottomSheet<LayoutDisplayStatisticalForTimeBottomSheetBinding>(), OnClickItem {

    private var adapter: TimeStatisticalAdapter? = null

    override fun layoutRes(): Int = R.layout.layout_display_statistical_for_time_bottom_sheet

    override fun initView() {
        binding.rcvTime.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = TimeStatisticalAdapter(this)
        binding.rcvTime.adapter = adapter

        binding.tvCancel.setOnSingClickListener { dismiss() }
    }

    override fun onClickItem(item: TimeStatistical, position: Int) {
        dismiss()
        onClickItemTime?.onClickItemTime(item)
        adapter?.setSelected(position)
    }
}

interface OnClickItemTime {
    fun onClickItemTime(item: TimeStatistical)
}
