package vn.ncsc.visafe.ui.create.workspace.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.DialogCreateSuccessWorkspaceBinding
import vn.ncsc.visafe.ui.create.group.access_manager.Action

class DialogCreateSuccessWorkSpace : DialogFragment() {
    private lateinit var binding: DialogCreateSuccessWorkspaceBinding
    private var mOnClickListener: ((Action) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_create_success_workspace,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
    }

    private fun initView() {
        binding.tvNext.setOnClickListener {
            mOnClickListener?.invoke(Action.CONFIRM)
            dismiss()
        }
    }

    fun setOnClickListener(onClickAction: (Action) -> Unit) {
        mOnClickListener = onClickAction
    }
}