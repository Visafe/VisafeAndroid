package com.vn.visafe_android.ui.create.group.protected_group

import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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

    private var createGroupActivity: CreateGroupActivity? = null

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
            createGroupActivity?.somethingObject?.groupName = binding.editNameGroup.text.toString()
            createGroupActivity?.addFragment(BlockTracingAndAdsFragment.newInstance())
            createGroupActivity?.showReset()
            hiddenKeyboard()
        }

        binding.checkBoxChild.setOnCheckedChangeListener { _, _ ->
            enableButton()
            createGroupActivity?.somethingObject?.typeChild = binding.checkBoxAdult.isChecked
        }

        binding.checkBoxAdult.setOnCheckedChangeListener { _, _ ->
            enableButton()
            createGroupActivity?.somethingObject?.typePeople = binding.checkBoxAdult.isChecked
        }

        binding.checkBoxElder.setOnCheckedChangeListener { _, _ ->
            enableButton()
            createGroupActivity?.somethingObject?.typeOldPeople = binding.checkBoxElder.isChecked
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