package vn.ncsc.visafe.ui.group.dashboard

import android.content.Intent
import androidx.core.os.bundleOf
import vn.ncsc.visafe.ui.group.detail.GroupDetailActivity
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentGroupDashboardConfigurationBinding
import vn.ncsc.visafe.model.GroupData

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

    private fun naviToGroupDetail(data: GroupData, type: String) {
        val intent = Intent(requireContext(), GroupDetailActivity::class.java)
        startActivity(intent)
    }
}