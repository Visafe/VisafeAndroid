package vn.ncsc.visafe.ui.create.group

import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentWelcomeCreateGroupBinding
import vn.ncsc.visafe.model.TYPE_WORKSPACES
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.ui.create.group.protected_group.ProtectedGroupFragment
import vn.ncsc.visafe.utils.setOnSingClickListener

class WelcomeCreateGroupFragment : BaseFragment<FragmentWelcomeCreateGroupBinding>() {

    private var createGroupActivity: CreateGroupActivity? = null
    private var workspaceGroupData: WorkspaceGroupData? = null

    companion object {
        const val DATA_WORKSPACE = "DATA_WORKSPACE"
        fun newInstance(workspaceGroupData: WorkspaceGroupData): WelcomeCreateGroupFragment {
            val args = Bundle()
            args.putParcelable(DATA_WORKSPACE, workspaceGroupData)
            val fragment = WelcomeCreateGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workspaceGroupData = it.getParcelable(DATA_WORKSPACE)
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_welcome_create_group

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun initView() {
        val typeWorkspaces = TYPE_WORKSPACES.fromIsTypeWorkSpaces(workspaceGroupData?.type)
        when (typeWorkspaces?.type) {
            TYPE_WORKSPACES.FAMILY.type -> {
                binding.ivGroup.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.bg_welcome_protected_family
                    )
                })
                binding.tvTitle.text = getString(R.string.protect_family)
                binding.tvContent.text = getString(R.string.protect_content)
            }
            TYPE_WORKSPACES.ENTERPRISE.type -> {
                binding.ivGroup.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.bg_welcom_protected_enterprise
                    )
                })
                binding.tvTitle.text = getString(R.string.protected_enterprise)
                binding.tvContent.text = getString(R.string.protected_enterprise_content)
            }
            TYPE_WORKSPACES.SCHOOL.type -> {
                binding.ivGroup.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.bg_welcome_protected_family
                    )
                })
                binding.tvTitle.text = getString(R.string.protect_family)
                binding.tvContent.text = getString(R.string.protect_content)
            }
        }
        binding.btnStart.setOnSingClickListener {
            createGroupActivity?.addFragment(ProtectedGroupFragment.newInstance())
        }
        binding.ivBack.setOnClickListener {
            createGroupActivity?.onBackPressed()
        }
    }
}