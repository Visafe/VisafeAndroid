package vn.ncsc.visafe.ui.create.group

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import vn.ncsc.visafe.databinding.SuccessDialogFragmentBinding
import vn.ncsc.visafe.ui.create.group.access_manager.Action

class SuccessDialogFragment : DialogFragment() {
    private var mOnClickListener: ((Action) -> Unit)? = null

    companion object {
        const val TITLE_DIALOG = "TITLE_DIALOG"
        const val CONTENT_DIALOG = "CONTENT_DIALOG"
        const val TEXT_BUTTON_DIALOG = "TEXT_BUTTON_DIALOG"

        fun newInstance(): SuccessDialogFragment {
            val args = Bundle()

            val fragment = SuccessDialogFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(title: String, content: String): SuccessDialogFragment {
            val fragment = SuccessDialogFragment()
            fragment.arguments = bundleOf(
                Pair(TITLE_DIALOG, title),
                Pair(CONTENT_DIALOG, content)
            )
            return fragment
        }

        fun newInstance(title: String, content: String, textButton: String): SuccessDialogFragment {
            val fragment = SuccessDialogFragment()
            fragment.arguments = bundleOf(
                Pair(TITLE_DIALOG, title),
                Pair(CONTENT_DIALOG, content),
                Pair(TEXT_BUTTON_DIALOG, textButton)
            )
            return fragment
        }
    }

    lateinit var binding: SuccessDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dialog: Dialog? = dialog
        if (dialog != null) {
            with(dialog.window) {
                this?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                this?.requestFeature(Window.FEATURE_NO_TITLE)
                this?.setGravity(Gravity.BOTTOM)
            }
            dialog.setCancelable(false)
        }

        binding =
            SuccessDialogFragmentBinding.inflate(
                LayoutInflater.from(context),
                container,
                false
            )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvNext.setOnClickListener {
            mOnClickListener?.invoke(Action.CONFIRM)
            dismiss()
        }
        if (arguments != null && requireArguments().getString(TITLE_DIALOG, "").isNotEmpty()) {
            binding.tvTitle.text = requireArguments().getString(TITLE_DIALOG, "")
        }
        if (arguments != null && requireArguments().getString(CONTENT_DIALOG, "").isNotEmpty()) {
            binding.tvTitle2.text = requireArguments().getString(CONTENT_DIALOG, "")
            binding.tvTitle2.visibility = View.GONE
        }
        if (arguments != null && requireArguments().getString(TEXT_BUTTON_DIALOG, "").isNotEmpty()) {
            binding.tvNext.text = requireArguments().getString(TEXT_BUTTON_DIALOG, "")
        }
    }

    fun setOnClickListener(onClickAction: (Action) -> Unit) {
        mOnClickListener = onClickAction
    }
}