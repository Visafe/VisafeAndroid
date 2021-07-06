package com.vn.visafe_android.ui.group.dashboard

import android.content.Intent
import androidx.core.os.bundleOf
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupDashboardConfigurationBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.ui.group.detail.GroupDetailActivity
import com.vn.visafe_android.utils.setOnSingClickListener

class GroupDashboardConfigurationFragment :
    BaseFragment<FragmentGroupDashboardConfigurationBinding>() {

    companion object {
        const val DATA_KEY = "DATA_KEY"

        fun newInstance(data: GroupData): GroupDashboardConfigurationFragment {
            val fragment = GroupDashboardConfigurationFragment()
            fragment.arguments = bundleOf(
                Pair(DATA_KEY, data)
            )
            return fragment
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_group_dashboard_configuration

    override fun initView() {
        val data = arguments?.get(DATA_KEY) as GroupData
        setGroupData(data)
        binding.rlBlockTracking.setOnSingClickListener {
            naviToGroupDetail(data, GroupDetailActivity.TYPE_CHAN_THEO_DOI_QUANG_CAO)
        }
        binding.rlAccessManagement.setOnSingClickListener {
            naviToGroupDetail(data, GroupDetailActivity.TYPE_QUAN_LY_TRUY_CAP)
        }
        binding.rlProtectTime.setOnSingClickListener {
            naviToGroupDetail(data, GroupDetailActivity.TYPE_KHUNG_GIO_BAO_VE)
        }
        binding.rlDeviceManagement.setOnSingClickListener {
            naviToGroupDetail(data, GroupDetailActivity.TYPE_QUAN_LY_THIET_BI)
        }
        binding.rlMemberManagement.setOnSingClickListener {
            naviToGroupDetail(data, GroupDetailActivity.TYPE_QUAN_LY_THANH_VIEN)
        }
    }

    private fun setGroupData(data: GroupData) {
        binding.tvNameGroup.text = data.name
    }

    private fun naviToGroupDetail(data: GroupData, type: String) {
        val intent = Intent(requireContext(), GroupDetailActivity::class.java)
        intent.putExtra(GroupDetailActivity.TYPE_GROUP_DATA_KEY, data)
        intent.putExtra(GroupDetailActivity.TYPE_CONFIGURATION_KEY, type)
        startActivity(intent)
    }
}