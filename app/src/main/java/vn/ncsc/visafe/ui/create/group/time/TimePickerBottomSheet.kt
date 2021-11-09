package vn.ncsc.visafe.ui.create.group.time

import android.os.Bundle
import android.view.View
import android.widget.Toast
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseDialogBottomSheet
import vn.ncsc.visafe.databinding.LayoutTimePickerBottomSheetBinding
import vn.ncsc.visafe.model.TimeProtection
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import java.text.SimpleDateFormat
import java.util.*

class TimePickerBottomSheet : BaseDialogBottomSheet<LayoutTimePickerBottomSheetBinding>() {

    companion object {
        fun newInstance(data: TimeProtection?): TimePickerBottomSheet {
            val args = Bundle()
            args.putParcelable("data", data)
            val fragment = TimePickerBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    private var mOnClickConfirmListener: ((TimeProtection, Action) -> Unit)? = null

    private lateinit var mAction: Action
    private var data: TimeProtection? = null

    override fun initView() {

        data = arguments?.getParcelable<TimeProtection>("data")

        mAction = if (data == null) {
            binding.tvTitle.text = getString(R.string.add_time)
            binding.tvCancel.text = getString(R.string.cancel)
            Action.ADD
        } else {
            binding.tvTitle.text = getString(R.string.change_time)
            binding.tvCancel.text = getString(R.string.delete)

            binding.itemProtectionAllDay.setChecked(data?.isProtectionAllDay ?: false)
            if (data!!.isProtectionAllDay) {
                binding.groupTime.visibility = View.GONE
            } else {
                binding.groupTime.visibility = View.VISIBLE
                binding.edtStartTime.setText(data?.startTime)
                binding.edtEndTime.setText(data?.endTime)
            }


            Action.EDIT
        }

        binding.tvCancel.setOnClickListener {
            if (mAction == Action.EDIT) {
                data?.let { it1 -> mOnClickConfirmListener?.invoke(it1, Action.DELETE) }
            }
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            handle()
        }

        binding.itemProtectionAllDay.setOnSwitchChangeListener {
            binding.groupTime.visibility = if (it) View.GONE else View.VISIBLE
        }

    }

    fun setOnConfirmListener(onConfirmListener: (TimeProtection, Action) -> Unit) {
        mOnClickConfirmListener = onConfirmListener
    }

    private fun handle() {
        val isAllDay = binding.itemProtectionAllDay.isChecked()

        if (isAllDay) {
            mOnClickConfirmListener?.invoke(
                TimeProtection(
                    isProtectionAllDay = true,
                    isChecked = true
                ), mAction
            )
            dismiss()
            return
        }

        val startTime = binding.edtStartTime.getText()
        val endTime = binding.edtEndTime.getText()

        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.require_pick_time),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startDate = dateFormat.parse(startTime)
        if (startDate?.after(dateFormat.parse(endTime)) == true || startTime == endTime) {
            Toast.makeText(
                requireContext(),
                "Thời gian kết thúc phải lớn hơn thời gian bắt đầu",
                Toast.LENGTH_SHORT
            ).show()

            return
        } else {
            mOnClickConfirmListener?.invoke(
                TimeProtection(
                    startTime = startTime,
                    endTime = endTime,
                    isProtectionAllDay = false,
                    isChecked = data?.isChecked ?: true
                ), mAction
            )
            dismiss()
        }

    }

    override fun layoutRes(): Int = R.layout.layout_time_picker_bottom_sheet
}