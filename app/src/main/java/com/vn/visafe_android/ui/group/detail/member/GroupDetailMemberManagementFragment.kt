package com.vn.visafe_android.ui.group.detail.member

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupDetailMemberManagementBinding
import com.vn.visafe_android.ui.adapter.ScreenSlidePagerAdapter

class GroupDetailMemberManagementFragment :
    BaseFragment<FragmentGroupDetailMemberManagementBinding>() {
    private var pagerAdapter: ScreenSlidePagerAdapter? = null

    override fun layoutRes(): Int = R.layout.fragment_group_detail_member_management

    override fun initView() {
        pagerAdapter = ScreenSlidePagerAdapter(parentFragmentManager)
        pagerAdapter?.addFragment(MemberManagementUserFragment(), getString(R.string.member_user))
        pagerAdapter?.addFragment(MemberManagementAdminFragment(), getString(R.string.member_admin))

        binding.viewPager.swipeable = false
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = pagerAdapter

        binding.tabBar.setupWithViewPager(binding.viewPager)
    }
}