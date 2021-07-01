package com.vn.visafe_android.ui.create.workspace

import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentProtectWorkspaceBinding
import com.vn.visafe_android.model.TYPE_WORKSPACES
import com.vn.visafe_android.ui.create.group.block_tracking.BlockTracingAndAdsFragment

class ProtectWorkspaceFragment : BaseFragment<FragmentProtectWorkspaceBinding>() {
    private var type = ""

    companion object {
        fun newInstance(): ProtectWorkspaceFragment {
            val args = Bundle()
            val fragment = ProtectWorkspaceFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createWorkspaceActivity: CreateWorkspaceActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_protect_workspace

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateWorkspaceActivity) {
            createWorkspaceActivity = context
        }
    }

    override fun initView() {
        enableButton()
        binding.editNameWorkspace.addTextChangedListener {
            enableButton()
        }

        binding.tvNext.setOnClickListener {
            createWorkspaceActivity?.groupData?.name = binding.editNameWorkspace.text.toString()
            createWorkspaceActivity?.groupData?.type = type
            createWorkspaceActivity?.addFragment(SetupProtectWorkspaceFragment.newInstance())
            hiddenKeyboard()
        }

        binding.checkBoxChinhPhu.setOnClickListener {
            enableButton()
            type = TYPE_WORKSPACES.GOVERNMENT_ORGANIZATION.type
            binding.ivWorkspace.setImageResource(TYPE_WORKSPACES.GOVERNMENT_ORGANIZATION.resDrawableIcon)
            binding.checkBoxCompany.isChecked = false
            binding.checkBoxFamily.isChecked = false
            binding.checkBoxSchool.isChecked = false
            binding.checkBoxChinhPhu.isChecked = true
        }

        binding.checkBoxCompany.setOnClickListener {
            enableButton()
            type = TYPE_WORKSPACES.ENTERPRISE.type
            binding.ivWorkspace.setImageResource(TYPE_WORKSPACES.ENTERPRISE.resDrawableIcon)
            binding.checkBoxChinhPhu.isChecked = false
            binding.checkBoxFamily.isChecked = false
            binding.checkBoxSchool.isChecked = false
            binding.checkBoxCompany.isChecked = true
        }

        binding.checkBoxFamily.setOnClickListener {
            enableButton()
            type = TYPE_WORKSPACES.FAMILY.type
            binding.ivWorkspace.setImageResource(TYPE_WORKSPACES.FAMILY.resDrawableIcon)
            binding.checkBoxChinhPhu.isChecked = false
            binding.checkBoxCompany.isChecked = false
            binding.checkBoxSchool.isChecked = false
            binding.checkBoxFamily.isChecked = true
        }

        binding.checkBoxSchool.setOnClickListener {
            enableButton()
            type = TYPE_WORKSPACES.SCHOOL.type
            binding.ivWorkspace.setImageResource(TYPE_WORKSPACES.SCHOOL.resDrawableIcon)
            binding.checkBoxChinhPhu.isChecked = false
            binding.checkBoxCompany.isChecked = false
            binding.checkBoxFamily.isChecked = false
            binding.checkBoxSchool.isChecked = true
        }

        setHideKeyboardFocus(binding.root)
    }

    private fun enableButton() {
        val workspaceName = binding.editNameWorkspace.text.toString()

        if (workspaceName.isNotBlank()
            && (binding.checkBoxChinhPhu.isChecked
                    || binding.checkBoxCompany.isChecked
                    || binding.checkBoxSchool.isChecked
                    || binding.checkBoxFamily.isChecked)
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