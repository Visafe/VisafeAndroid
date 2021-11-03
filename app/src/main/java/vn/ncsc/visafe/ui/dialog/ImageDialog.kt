package vn.ncsc.visafe.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import vn.ncsc.visafe.R
import vn.ncsc.visafe.databinding.DialogImageBinding
import vn.ncsc.visafe.ui.create.group.access_manager.Action

class ImageDialog : DialogFragment() {
    companion object {
        const val TYPE_DIALOG_KEY = "TYPE_DIALOG_KEY"

        const val TYPE_ADD_VPN = "TYPE_ADD_VPN"
        const val TYPE_TURN_ON_NOTI = "TYPE_TURN_ON_NOTI"

        fun newsIntance(type: String) : ImageDialog {
            val dialog = ImageDialog()
            dialog.arguments = bundleOf(
                Pair(TYPE_DIALOG_KEY, type)
            )
            return dialog
        }
    }
    private lateinit var binding: DialogImageBinding
    private var mOnClickListener: ((Action) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_image,
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
        binding.tvConfirm.setOnClickListener {
            mOnClickListener?.invoke(Action.CONFIRM)
            dismiss()
        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        val type = arguments?.getString(TYPE_DIALOG_KEY, "") ?: ""
        if (type.isNotEmpty()) {
            when (type) {
                TYPE_ADD_VPN -> {
                    binding.tvTitle.text = getString(R.string.add_vpn_dialog)
                    binding.tvContent.text = getString(R.string.add_vpn_content)
                    binding.tvConfirm.text = getString(R.string.cho_phep)
                    binding.ivCheck.setImageResource(R.drawable.ic_vpn)
                }
                TYPE_TURN_ON_NOTI -> {
                    binding.tvTitle.text = getString(R.string.turn_on_noti_title)
                    binding.tvContent.text = getString(R.string.turn_on_noti_content)
                    binding.tvConfirm.text = getString(R.string.bat_thong_bao)
                    binding.ivCheck.setImageResource(R.drawable.ic_turn_on_noti)
                }
            }
        }
    }

    fun setOnClickListener(onClickAction: (Action) -> Unit) {
        mOnClickListener = onClickAction
    }
}