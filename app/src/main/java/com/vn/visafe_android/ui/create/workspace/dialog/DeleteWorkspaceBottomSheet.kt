package com.vn.visafe_android.ui.create.workspace.dialog

import androidx.core.os.bundleOf
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseDialogBottomSheet
import com.vn.visafe_android.databinding.LayoutDeleteWorkspaceBottomSheetBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.create.group.access_manager.Action

class DeleteWorkspaceBottomSheet :
    BaseDialogBottomSheet<LayoutDeleteWorkspaceBottomSheetBinding>() {
    private var mOnClickListener: ((Action) -> Unit)? = null

    companion object {

        fun newInstance(data: WorkspaceGroupData): DeleteWorkspaceBottomSheet {
            val fragment = DeleteWorkspaceBottomSheet()
            fragment.arguments = bundleOf(
                Pair("data", data)
            )
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.layout_delete_workspace_bottom_sheet

    override fun initView() {
        val data = arguments?.getParcelable<WorkspaceGroupData>("data")
        data?.let {
            binding.tvGroup.text = getString(R.string.delete_workspace_content, data.name)
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            mOnClickListener?.invoke(Action.CONFIRM)
            dismiss()
        }
    }

    fun setOnClickListener(onClickAction: (Action) -> Unit) {
        mOnClickListener = onClickAction
    }
}