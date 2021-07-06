package com.vn.visafe_android.ui.dialog

import android.view.View
import androidx.core.os.bundleOf
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseDialogBottomSheet
import com.vn.visafe_android.databinding.LayoutVisafeDialogBottomSheetBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.access_manager.Action

class VisafeDialogBottomSheet : BaseDialogBottomSheet<LayoutVisafeDialogBottomSheetBinding>() {
    companion object {
        const val TYPE_DIALOG_KEY = "TYPE_DIALOG_KEY"
        const val TITLE = "TITLE"
        const val NAME = "NAME"
        const val TITLE_EDIT = "TITLE_EDIT"
        const val TITLE_DELETE = "TITLE_DELETE"
        const val EDIT_HINT = "EDIT_HINT"
        const val EDIT_NAME = "EDIT_NAME"

        const val TYPE_EDIT = "TYPE_EDIT"
        const val TYPE_CONFIRM = "TYPE_CONFIRM"
        const val TYPE_ADD = "TYPE_ADD"
        const val TYPE_SAVE = "TYPE_SAVE"

        fun newInstance(title: String, name: String, type: String): VisafeDialogBottomSheet {
            val fragment = VisafeDialogBottomSheet()
            fragment.arguments = bundleOf(
                Pair(TYPE_DIALOG_KEY, type),
                Pair(TITLE, title),
                Pair(NAME, name)
            )
            return fragment
        }

        fun newInstance(title: String, name: String, type: String, titleEdit: String, titleDelete: String): VisafeDialogBottomSheet {
            val fragment = VisafeDialogBottomSheet()
            fragment.arguments = bundleOf(
                Pair(TYPE_DIALOG_KEY, type),
                Pair(TITLE, title),
                Pair(TITLE_EDIT, titleEdit),
                Pair(TITLE_DELETE, titleDelete),
                Pair(NAME, name)
            )
            return fragment
        }

        fun newInstanceEdit(title: String, name: String, type: String, hint: String, editText: String): VisafeDialogBottomSheet {
            val fragment = VisafeDialogBottomSheet()
            fragment.arguments = bundleOf(
                Pair(TYPE_DIALOG_KEY, type),
                Pair(TITLE, title),
                Pair(EDIT_HINT, hint),
                Pair(NAME, name),
                Pair(EDIT_NAME, editText)
            )
            return fragment
        }
    }
    private var mOnClickListener: ((String, Action) -> Unit)? = null

    override fun layoutRes(): Int = R.layout.layout_visafe_dialog_bottom_sheet

    override fun initView() {
        binding.tvCancel.setOnClickListener {
            hiddenKeyboard()
            dismiss()
        }
        binding.tvEdit.setOnClickListener {
            hiddenKeyboard()
            mOnClickListener?.invoke(binding.edtInput.text.toString().trim(), Action.EDIT)
            dismiss()
        }
        binding.tvDelete.setOnClickListener {
            hiddenKeyboard()
            mOnClickListener?.invoke(binding.edtInput.text.toString().trim(), Action.DELETE)
            dismiss()
        }
        binding.tvSave.setOnClickListener {
            hiddenKeyboard()
            mOnClickListener?.invoke(binding.edtInput.text.toString().trim(), Action.SAVE)
            dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            hiddenKeyboard()
            mOnClickListener?.invoke(binding.edtInput.text.toString().trim(), Action.CONFIRM)
            dismiss()
        }

        val title = arguments?.getString(TITLE, "")
        binding.tvTitle.text = title
        val name = arguments?.getString(NAME, "")
        binding.tvName.text = name
        val titleEdit = arguments?.getString(TITLE_EDIT, "")
        binding.tvEdit.text = titleEdit
        val titleDelete = arguments?.getString(TITLE_DELETE, "")
        binding.tvDelete.text = titleDelete
        val hint = arguments?.getString(EDIT_HINT, "")
        binding.edtInput.hint = hint
        val editName = arguments?.getString(EDIT_NAME, "")
        binding.edtInput.setText(editName)

        val type = arguments?.getString(TYPE_DIALOG_KEY, "")
        when (type) {
            TYPE_ADD -> {
                showLayoutAdd()
            }
            TYPE_CONFIRM -> {
                showLayoutConfirm()
            }
            TYPE_EDIT -> {
                showLayoutEdit()
            }
            TYPE_SAVE -> {
                showLayoutSave()
            }
        }
    }

    private fun showLayoutEdit() {
        binding.llEdit.visibility = View.VISIBLE
        binding.edtInput.visibility = View.GONE
        binding.tvConfirm.visibility = View.GONE
        binding.tvSave.visibility = View.GONE
    }

    private fun showLayoutConfirm() {
        binding.tvTitle.visibility = View.GONE
        binding.llEdit.visibility = View.GONE
        binding.edtInput.visibility = View.GONE
        binding.tvConfirm.visibility = View.VISIBLE
        binding.tvSave.visibility = View.GONE
        binding.tvCancel.text = getString(R.string.no)
    }

    private fun showLayoutAdd() {
        binding.llEdit.visibility = View.GONE
        binding.edtInput.visibility = View.VISIBLE
        binding.tvConfirm.visibility = View.VISIBLE
        binding.tvSave.visibility = View.GONE
    }

    private fun showLayoutSave() {
        binding.tvTitle.visibility = View.VISIBLE
        binding.llEdit.visibility = View.GONE
        binding.edtInput.visibility = View.VISIBLE
        binding.tvConfirm.visibility = View.GONE
        binding.tvSave.visibility = View.VISIBLE
    }

    fun setOnClickListener(onConfirmListener: (String, Action) -> Unit) {
        mOnClickListener = onConfirmListener
    }
}