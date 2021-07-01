package com.vn.visafe_android.ui.create.workspace.dialog

import androidx.core.os.bundleOf
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseDialogBottomSheet
import com.vn.visafe_android.databinding.LayoutEditWorkSpaceBottomSheetBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.create.group.access_manager.Action

class EditWorkspaceBottomSheet : BaseDialogBottomSheet<LayoutEditWorkSpaceBottomSheetBinding>() {

    private var mOnClickListener: ((Action) -> Unit)? = null

    override fun layoutRes(): Int = R.layout.layout_edit_work_space_bottom_sheet

    companion object {
        fun newInstance(data: WorkspaceGroupData): EditWorkspaceBottomSheet {
            val fragment = EditWorkspaceBottomSheet()
            fragment.arguments = bundleOf(
                Pair("data", data)
            )
            return fragment
        }
    }

    override fun initView() {
        val data = arguments?.getParcelable<WorkspaceGroupData>("data")

        data?.let {
            binding.tvNameGroup.text = data.name
        }
        binding.tvEdit.setOnClickListener {
            mOnClickListener?.invoke(Action.EDIT)
            dismiss()
        }
        binding.tvDelete.setOnClickListener {
            mOnClickListener?.invoke(Action.DELETE)
            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnClickListener(onClickAction: (Action) -> Unit) {
        mOnClickListener = onClickAction
    }
}