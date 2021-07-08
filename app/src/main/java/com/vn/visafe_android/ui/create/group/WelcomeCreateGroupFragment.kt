package com.vn.visafe_android.ui.create.group

import android.content.Context
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentWelcomeCreateGroupBinding
import com.vn.visafe_android.ui.create.group.protected_group.ProtectedGroupFragment
import com.vn.visafe_android.utils.setOnSingClickListener

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