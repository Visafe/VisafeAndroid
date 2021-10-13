package vn.ncsc.visafe.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.text.InputFilter
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseDialogBottomSheet
import vn.ncsc.visafe.databinding.LayoutVisafeDialogBottomSheetBinding
import vn.ncsc.visafe.ui.create.group.access_manager.Action


class VisafeDialogBottomSheet : BaseDialogBottomSheet<LayoutVisafeDialogBottomSheetBinding>() {
    companion object {
        const val TYPE_DIALOG_KEY = "TYPE_DIALOG_KEY"
        const val TITLE = "TITLE"
        const val NAME = "NAME"
        const val TITLE_EDIT = "TITLE_EDIT"
        const val RES_LEFT_TEXT = "RES_LEFT_TEXT"
        const val TITLE_DELETE = "TITLE_DELETE"
        const val EDIT_HINT = "EDIT_HINT"
        const val EDIT_NAME = "EDIT_NAME"
        const val TEXT_WARNING = "TEXT_WARNING"
        const val MIN_INPUT = "MIN_INPUT"
        const val MAX_INPUT = "MAX_INPUT"

        const val TYPE_EDIT_DELETE = "TYPE_EDIT_DELETE"
        const val TYPE_CONFIRM_CANCEL = "TYPE_CONFIRM_CANCEL"
        const val TYPE_INPUT_CONFIRM = "TYPE_INPUT_CONFIRM"
        const val TYPE_INPUT_SAVE = "TYPE_INPUT_SAVE"

        fun newInstance(title: String, name: String, type: String): VisafeDialogBottomSheet {
            val fragment = VisafeDialogBottomSheet()
            fragment.arguments = bundleOf(
                Pair(TYPE_DIALOG_KEY, type),
                Pair(TITLE, title),
                Pair(NAME, name)
            )
            return fragment
        }

        fun newInstance(
            title: String?,
            name: String?,
            type: String?,
            titleEdit: String?,
            titleDelete: String?
        ): VisafeDialogBottomSheet {
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

        fun newInstance(
            title: String?,
            name: String?,
            type: String?,
            titleEdit: String?,
            resLeftText: Int?,
            titleDelete: String?
        ): VisafeDialogBottomSheet {
            val fragment = VisafeDialogBottomSheet()
            fragment.arguments = bundleOf(
                Pair(TYPE_DIALOG_KEY, type),
                Pair(TITLE, title),
                Pair(TITLE_EDIT, titleEdit),
                Pair(RES_LEFT_TEXT, resLeftText),
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

        fun newInstanceEdit(
            title: String,
            name: String,
            type: String,
            hint: String,
            editText: String,
            textWarning: String,
            minInput: Int = 1,
            maxInput: Int = 300
        ): VisafeDialogBottomSheet {
            val fragment = VisafeDialogBottomSheet()
            fragment.arguments = bundleOf(
                Pair(TYPE_DIALOG_KEY, type),
                Pair(TITLE, title),
                Pair(EDIT_HINT, hint),
                Pair(NAME, name),
                Pair(EDIT_NAME, editText),
                Pair(TEXT_WARNING, textWarning),
                Pair(MIN_INPUT, minInput),
                Pair(MAX_INPUT, maxInput)
            )
            return fragment
        }
    }

    private var mOnClickListener: ((String, Action) -> Unit)? = null

    override fun layoutRes(): Int = R.layout.layout_visafe_dialog_bottom_sheet

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.layout_visafe_dialog_bottom_sheet, null)
        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

//    var filter = InputFilter { source, start, end, dest, dstart, dend ->
//        for (i in start until end) {
//            if (Character.isWhitespace(source[i])) {
//                return@InputFilter ""
//            }
//        }
//        null
//    }

    override fun initView() {
//        binding.edtInput.filters = arrayOf(filter, InputFilter.LengthFilter(300))
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
        binding.tvTitle.visibility = if (title.isNullOrEmpty()) View.GONE else View.VISIBLE
        val name = arguments?.getString(NAME, "")
        binding.tvName.text = name
        binding.tvName.visibility = if (name.isNullOrEmpty()) View.GONE else View.VISIBLE
        val titleEdit = arguments?.getString(TITLE_EDIT, "")
        binding.tvEdit.text = titleEdit
        val resLeftText = arguments?.getInt(RES_LEFT_TEXT, 0)
        if (resLeftText != null && resLeftText != 0) {
            binding.tvEdit.setCompoundDrawablesWithIntrinsicBounds(resLeftText, 0, 0, 0)
        }
        val titleDelete = arguments?.getString(TITLE_DELETE, "")
        binding.tvDelete.text = titleDelete
        val hint = arguments?.getString(EDIT_HINT, "")
        binding.edtInput.hint = hint
        val editName = arguments?.getString(EDIT_NAME, "")
        binding.edtInput.setText(editName)
        val type = arguments?.getString(TYPE_DIALOG_KEY, "")
        when (type) {
            TYPE_INPUT_CONFIRM -> {
                showLayoutAdd()
                binding.edtInput.addTextChangedListener {
                    val minInput = arguments?.getInt(MIN_INPUT, 1)
                    val maxInput = arguments?.getInt(MAX_INPUT, 300)
                    if (binding.edtInput.length() in minInput!!..maxInput!!) {
                        binding.edtInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext)
                        binding.tvWarning.text = ""
                        binding.tvWarning.visibility = View.GONE
                    } else {
                        val warnings = arguments?.getString(TEXT_WARNING, "")
                        binding.edtInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_warning)
                        binding.tvWarning.text = warnings
                        binding.tvWarning.visibility = if (warnings?.isNotEmpty() == true) View.VISIBLE else View.GONE
                    }
                    enableButtonConfirm()
                }
                enableButtonConfirm()
            }
            TYPE_CONFIRM_CANCEL -> {
                showLayoutConfirm()
            }
            TYPE_EDIT_DELETE -> {
                showLayoutEdit()
            }
            TYPE_INPUT_SAVE -> {
                showLayoutSave()
                binding.edtInput.addTextChangedListener {
                    val minInput = arguments?.getInt(MIN_INPUT, 1)
                    val maxInput = arguments?.getInt(MAX_INPUT, 300)
                    if (binding.edtInput.length() in minInput!!..maxInput!!) {
                        binding.edtInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext)
                        binding.tvWarning.text = ""
                        binding.tvWarning.visibility = View.GONE
                    } else {
                        val warnings = arguments?.getString(TEXT_WARNING, "")
                        binding.edtInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edittext_warning)
                        binding.tvWarning.text = warnings
                        binding.tvWarning.visibility = if (warnings?.isNotEmpty() == true) View.VISIBLE else View.GONE
                    }
                    enableButtonSave()
                }
                enableButtonSave()
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
        binding.tvTitle.visibility = View.GONE
        binding.llEdit.visibility = View.GONE
        binding.edtInput.visibility = View.VISIBLE
        binding.tvConfirm.visibility = View.GONE
        binding.tvSave.visibility = View.VISIBLE
    }

    fun setOnClickListener(onConfirmListener: (String, Action) -> Unit) {
        mOnClickListener = onConfirmListener
    }

    private fun enableButtonConfirm() {
        val edtInput = binding.edtInput.text.toString()
        val minInput = arguments?.getInt(MIN_INPUT, 1)
        val maxInput = arguments?.getInt(MAX_INPUT, 300)
        if (edtInput.isNotBlank() && edtInput.length in minInput!!..maxInput!!) {
            with(binding.tvConfirm) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_FFB31F,
                        requireContext().theme
                    )

                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                isEnabled = true
            }
        } else {
            with(binding.tvConfirm) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_F8F8F8,
                        requireContext().theme
                    )
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_AAAAAA
                    )
                )
                isEnabled = false
            }
        }
    }

    private fun enableButtonSave() {
        val edtInput = binding.edtInput.text.toString()
        val minInput = arguments?.getInt(MIN_INPUT, 1)
        val maxInput = arguments?.getInt(MAX_INPUT, 300)
        if (edtInput.isNotBlank() && edtInput.length in minInput!!..maxInput!!) {
            with(binding.tvSave) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_FFB31F,
                        requireContext().theme
                    )

                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                isEnabled = true
            }
        } else {
            with(binding.tvSave) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_F8F8F8,
                        requireContext().theme
                    )
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_AAAAAA
                    )
                )
                isEnabled = false
            }
        }
    }
}