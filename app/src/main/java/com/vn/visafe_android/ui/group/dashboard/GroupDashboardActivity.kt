package com.vn.visafe_android.ui.group.dashboard

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityGroupDashboardBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.ui.adapter.ScreenSlidePagerAdapter
import com.vn.visafe_android.utils.OnSingleClickListener

class GroupDashboardActivity : BaseActivity() {
    companion object {
        const val GROUP_DATA_KEY = "GROUP_DATA_KEY"
    }

    lateinit var binding: ActivityGroupDashboardBinding
    private var pagerAdapter: ScreenSlidePagerAdapter? = null
    private var groupDashboardOverViewFragment = GroupDashboardOverViewFragment()
    private var groupDashboardConfigurationFragment: GroupDashboardConfigurationFragment? = null
    private var groupData: GroupData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        groupData = intent.getParcelableExtra<GroupData>(GROUP_DATA_KEY)
        if (groupData != null) {
            binding.toolbar.setTitleToolbar(groupData?.name!!)
        }

        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        pagerAdapter?.addFragment(groupDashboardOverViewFragment, "Tổng quan")
        groupDashboardConfigurationFragment = GroupDashboardConfigurationFragment.newInstance(groupData!!)
        pagerAdapter?.addFragment(groupDashboardConfigurationFragment!!, "Cấu hình nhóm")

        binding.viewPager.swipeable = false
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = pagerAdapter

        binding.tabBar.setupWithViewPager(binding.viewPager)

        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }

        })
    }
}