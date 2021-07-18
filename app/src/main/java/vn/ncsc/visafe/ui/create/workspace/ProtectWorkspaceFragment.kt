package vn.ncsc.visafe.ui.create.workspace

import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentProtectWorkspaceBinding
import vn.ncsc.visafe.model.TYPE_WORKSPACES
import vn.ncsc.visafe.utils.setOnSingClickListener

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
        binding.tvContent.text = HtmlCompat.fromHtml(getString(R.string.protect_group_content), HtmlCompat.FROM_HTML_MODE_LEGACY)
        enableButton()
        type = TYPE_WORKSPACES.SCHOOL.type
        binding.ivWorkspace.setImageResource(TYPE_WORKSPACES.SCHOOL.resDrawableIcon)
        binding.checkBoxChinhPhu.isChecked = false
        binding.checkBoxCompany.isChecked = false
        binding.checkBoxFamily.isChecked = false
        binding.checkBoxSchool.isChecked = true

        binding.editNameWorkspace.addTextChangedListener {
            enableButton()
        }

        binding.tvNext.setOnSingClickListener {
            createWorkspaceActivity?.createWorkSpaceRequest?.name = binding.editNameWorkspace.text.toString()
            createWorkspaceActivity?.createWorkSpaceRequest?.type = type
//            createWorkspaceActivity?.addFragment(SetupProtectWorkspaceFragment.newInstance())
            createWorkspaceActivity?.doCreateGroup()
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
//            && (binding.checkBoxChinhPhu.isChecked
//                    || binding.checkBoxCompany.isChecked
//                    || binding.checkBoxSchool.isChecked
//                    || binding.checkBoxFamily.isChecked)
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