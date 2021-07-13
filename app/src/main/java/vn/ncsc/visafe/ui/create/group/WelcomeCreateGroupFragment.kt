package vn.ncsc.visafe.ui.create.group

import android.content.Context
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentWelcomeCreateGroupBinding
import vn.ncsc.visafe.ui.create.group.protected_group.ProtectedGroupFragment
import vn.ncsc.visafe.utils.setOnSingClickListener

class WelcomeCreateGroupFragment : BaseFragment<FragmentWelcomeCreateGroupBinding>() {

    private var createGroupActivity: CreateGroupActivity? = null

    override fun layoutRes(): Int = R.layout.fragment_welcome_create_group

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateGroupActivity) {
            createGroupActivity = context
        }
    }

    override fun initView() {
        binding.btnStart.setOnSingClickListener {
            createGroupActivity?.addFragment(ProtectedGroupFragment.newInstance())
        }
        binding.ivBack.setOnClickListener {
            createGroupActivity?.onBackPressed()
        }
    }
}