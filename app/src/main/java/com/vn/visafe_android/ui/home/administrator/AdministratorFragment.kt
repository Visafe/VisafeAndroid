package com.vn.visafe_android.ui.home.administrator

import android.util.Log
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentAdministratorBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.adapter.ScreenSlidePagerAdapter

class AdministratorFragment : BaseFragment<FragmentAdministratorBinding>() {

    private var pagerAdapter: ScreenSlidePagerAdapter? = null
    private var overViewFragment = OverViewFragment()
    private var groupManagementFragment = GroupManagementFragment()
    private var configruationFragment = ConfigruationFragment()

    override fun layoutRes(): Int = R.layout.fragment_administrator

    override fun initView() {
        pagerAdapter = ScreenSlidePagerAdapter(parentFragmentManager)
        pagerAdapter?.addFragment(overViewFragment, "Tổng quan")
        pagerAdapter?.addFragment(groupManagementFragment, "Quản lý nhóm")
        pagerAdapter?.addFragment(configruationFragment, "Cấu hình")

        binding.viewPager.swipeable = false
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.adapter = pagerAdapter

        binding.tabBar.setupWithViewPager(binding.viewPager)
    }

    fun updateDataView(workspaceGroupData: WorkspaceGroupData) {
        val gson = Gson()
        Log.e("AdministratorFragment: ", gson.toJson(workspaceGroupData))
        groupManagementFragment.loadData(workspaceGroupData)
    }
}