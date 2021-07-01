package com.vn.visafe_android.ui.create.group

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.vn.visafe_android.databinding.SuccessDialogFragmentBinding
import com.vn.visafe_android.ui.create.group.access_manager.Action

class SuccessDialogFragment : DialogFragment() {
    private var mOnClickListener: ((Action) -> Unit)? = null

    companion object {
        fun newInstance(): SuccessDialogFragment {
            val args = Bundle()

            val fragment = SuccessDialogFragment()
            fragment.arguments = args
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
            binding.tvNext.setOnClickListener {
                mOnClickListener?.invoke(Action.CONFIRM)
                dismiss()
            }
        }
    }

    fun setOnClickListener(onClickAction: (Action) -> Unit) {
        mOnClickListener = onClickAction
    }
}