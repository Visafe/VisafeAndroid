package com.vn.visafe_android.ui.create.group.access_manager

import android.os.Bundle
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseDialogBottomSheet
import com.vn.visafe_android.databinding.LayoutEditWebsiteBottomSheetBinding
import com.vn.visafe_android.model.Subject

class EditWebsiteBottomSheet : BaseDialogBottomSheet<LayoutEditWebsiteBottomSheetBinding>() {

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"

        fun newInstance(data: Subject): EditWebsiteBottomSheet {
            val args = Bundle()
            args.putParcelable("data", data)
            val fragment = EditWebsiteBottomSheet()
            fragment.arguments = args
            return fragment
        }

    }

    private var mOnClickListener: ((Action) -> Unit)? = null

    override fun initView() {

        val data = arguments?.getParcelable<Subject>("data")
        data?.let {
            binding.tvLink.text = data.title
        }


        binding.tvEdit.setOnClickListener {
            editLink()
        }

        binding.tvDelete.setOnClickListener {
            deleteLink()
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnClickListener(onClickAction: (Action) -> Unit) {
        mOnClickListener = onClickAction
    }

    private fun deleteLink() {
        mOnClickListener?.invoke(Action.DELETE)
        dismiss()
    }

    private fun editLink() {
        mOnClickListener?.invoke(Action.EDIT)
        dismiss()
    }

    override fun layoutRes(): Int = R.layout.layout_edit_website_bottom_sheet
}