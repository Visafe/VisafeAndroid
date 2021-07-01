package com.vn.visafe_android.ui.create.group.access_manager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vn.visafe_android.R
import com.vn.visafe_android.databinding.LayoutAddWebsiteBottomSheetBinding
import com.vn.visafe_android.model.Subject
import com.vn.visafe_android.ui.create.group.CreateGroupActivity

class AddWebsiteBottomSheet : BottomSheetDialogFragment() {

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

    private lateinit var binding: LayoutAddWebsiteBottomSheetBinding

    private var mOnClickConfirmListener: ((Subject, Action) -> Unit)? = null

    private var mAction: Action? = null

    private var createGroupActivity: CreateGroupActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogStyle)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutAddWebsiteBottomSheetBinding.inflate(
            LayoutInflater.from(context),
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvGroup.text = createGroupActivity?.somethingObject?.groupName

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
            mAction?.let { mOnClickConfirmListener?.invoke(Subject(link, -1), it) }
            dismiss()
        } else {
            Toast.makeText(context, "require input", Toast.LENGTH_SHORT).show()
        }
    }

}