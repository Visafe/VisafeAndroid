package com.vn.visafe_android.ui.home.administrator

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentAdministratorBinding
import com.vn.visafe_android.ui.adapter.ScreenSlidePagerAdapter

class AdministratorFragment : BaseFragment<FragmentAdministratorBinding>() {

    var pagerAdapter: ScreenSlidePagerAdapter? = null

    companion object {
        fun newInstance() = AdministratorFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_administrator

    override fun initView() {
        pagerAdapter = ScreenSlidePagerAdapter(parentFragmentManager)
        pagerAdapter?.addFragment(OverViewFragment.newInstance(), "Tổng quan")
        pagerAdapter?.addFragment(GroupManagementFragment.newInstance(), "Quản lý nhóm")
        pagerAdapter?.addFragment(ConfigruationFragment.newInstance(), "Cấu hình")

        binding.viewPager.swipeable = false
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.adapter = pagerAdapter

        binding.tabBar.setupWithViewPager(binding.viewPager)
    }
}