package com.vn.visafe_android.ui.create.group.access_manager

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseDialogBottomSheet
import com.vn.visafe_android.databinding.LayoutAddWebsiteBottomSheetBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.CreateGroupActivity

class AddWebsiteBottomSheet : BaseDialogBottomSheet<LayoutAddWebsiteBottomSheetBinding>() {

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"

        fun newInstance(data: Subject?): AddWebsiteBottomSheet {
            val args = Bundle()
            args.putParcelable("data", data)
            val fragment = AddWebsiteBottomSheet()
            fragment.arguments = args
            return fragment
        }

    }

    private var mOnClickConfirmListener: ((Subject, Action) -> Unit)? = null

    private var mAction: Action? = null

    private var createGroupActivity: CreateGroupActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun initView() {

        val data = arguments?.getParcelable<Subject>("data")
        mAction = if (data == null) {
            Action.ADD
        } else {
            binding.editLink.setText(data.title)
            Action.EDIT
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvConfirm.setOnClickListener {
            addNewLink()
        }
    }

    fun setOnConfirmListener(onConfirmListener: (Subject, Action) -> Unit) {
        mOnClickConfirmListener = onConfirmListener
    }

    private fun addNewLink() {
        val link = binding.editLink.text.toString()
        if (link.isNotBlank()) {
            mAction?.let { mOnClickConfirmListener?.invoke(Subject(link, link, -1), it) }
            dismiss()
        } else {
            Toast.makeText(context, "require input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun layoutRes(): Int = R.layout.layout_add_website_bottom_sheet

}