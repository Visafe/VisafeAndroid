package com.vn.visafe_android.ui.create.group.access_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vn.visafe_android.databinding.LayoutEditWebsiteBottomSheetBinding
import com.vn.visafe_android.model.Subject

class EditWebsiteBottomSheet : BottomSheetDialogFragment() {

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

    private lateinit var binding: LayoutEditWebsiteBottomSheetBinding

    private var mOnClickListener: ((Action) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            LayoutEditWebsiteBottomSheetBinding.inflate(
                LayoutInflater.from(context),
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
}