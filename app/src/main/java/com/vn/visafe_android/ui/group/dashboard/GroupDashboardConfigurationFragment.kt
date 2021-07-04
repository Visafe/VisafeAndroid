package com.vn.visafe_android.ui.group.dashboard

import androidx.core.os.bundleOf
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentGroupDashboardConfigurationBinding
import com.vn.visafe_android.model.GroupData

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
    }

    private fun setGroupData(data: GroupData) {
        binding.tvNameGroup.text = data.name
    }
}