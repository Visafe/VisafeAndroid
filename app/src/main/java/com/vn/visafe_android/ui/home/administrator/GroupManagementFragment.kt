package com.vn.visafe_android.ui.home.administrator

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupManagementBinding

class GroupManagementFragment : BaseFragment<FragmentGroupManagementBinding>() {

    companion object {

        @JvmStatic
        fun newInstance() =
            GroupManagementFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_group_management

    override fun initView() {
    }
}