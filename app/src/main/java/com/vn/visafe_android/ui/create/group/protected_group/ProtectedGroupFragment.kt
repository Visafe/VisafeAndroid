package com.vn.visafe_android.ui.create.group.protected_group

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentProtectedGroupBinding
import com.vn.visafe_android.ui.create.group.CreateGroupActivity
import com.vn.visafe_android.ui.create.group.block_tracking.BlockTracingAndAdsFragment

class ProtectedGroupFragment : BaseFragment<FragmentProtectedGroupBinding>() {
    companion object {
        fun newInstance(): ProtectedGroupFragment {
            val args = Bundle()

            val fragment = ProtectedGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var children: String = ""
    private var adult: String = ""
    private var elderly: String = ""
    private var createGroupActivity: CreateGroupActivity? = null
    private var objectTypeList: MutableList<String> = mutableListOf()

    override fun layoutRes(): Int = R.layout.fragment_protected_group

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun initView() {
        enableButton()
        binding.editNameGroup.addTextChangedListener {
            enableButton()
        }

        binding.tvNext.setOnClickListener {
            if (adult.isNotEmpty()) {
                objectTypeList.add(adult)
            }
            if (children.isNotEmpty()) {
                objectTypeList.add(children)
            }
            if (elderly.isNotEmpty()) {
                objectTypeList.add(elderly)
            }
            createGroupActivity?.createGroupRequest?.name = binding.editNameGroup.text.toString()
            createGroupActivity?.createGroupRequest?.object_type = objectTypeList
            val gson = Gson()
            Log.e(
                "initView: ",
                "" + gson.toJson(createGroupActivity?.createGroupRequest)
            )
            createGroupActivity?.addFragment(BlockTracingAndAdsFragment.newInstance())
            createGroupActivity?.showReset()
            hiddenKeyboard()
        }

        binding.checkBoxChild.setOnCheckedChangeListener { _, isCheck ->
            children = if (isCheck) "children" else ""
            enableButton()
        }

        binding.checkBoxAdult.setOnCheckedChangeListener { _, isCheck ->
            adult = if (isCheck) "adult" else ""
            enableButton()
        }

        binding.checkBoxElder.setOnCheckedChangeListener { _, isCheck ->
            elderly = if (isCheck) "elderly" else ""
            enableButton()
        }
    }

    private fun enableButton() {
        val groupName = binding.editNameGroup.text.toString()
        if (groupName.isNotBlank()
            && (binding.checkBoxChild.isChecked
                    || binding.checkBoxAdult.isChecked
                    || binding.checkBoxElder.isChecked)
        ) {
            with(binding.tvNext) {
                backgroundTintList =
                    resources.getColorStateList(R.color.color_FFB31F, requireContext().theme)

                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                isEnabled = true
            }
        } else {
            with(binding.tvNext) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_F8F8F8,
                        requireContext().theme
                    )
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_111111
                    )
                )
                isEnabled = false
            }
        }
    }
}