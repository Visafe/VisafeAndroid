package vn.ncsc.visafe.ui.home

import androidx.recyclerview.widget.LinearLayoutManager
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseFragment
import vn.ncsc.visafe.databinding.FragmentConfigruationBinding
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.ui.adapter.ConfigAdapter
import vn.ncsc.visafe.utils.SetupConfig

class ConfigurationFragment : BaseFragment<FragmentConfigruationBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            ConfigurationFragment()
    }

    private var configAdapter: ConfigAdapter? = null

    override fun layoutRes(): Int = R.layout.fragment_configruation

    override fun initView() {
        configAdapter = ConfigAdapter()
        configAdapter?.onChangeConfig = object : ConfigAdapter.OnChangeConfig {
            override fun onChangeConfig(data: SetupConfig) {

            }

        }
        binding.rvConfig.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvConfig.adapter = configAdapter
    }

    fun loadData(workspaceGroupData: WorkspaceGroupData) {
        val a = SetupConfig.values()
        for (i in a) {
            when (i) {
                SetupConfig.CHONG_LUA_DAO_MANG -> {
                    i.selected = workspaceGroupData.phishingEnabled!!
                }
                SetupConfig.LUU_LICH_SU_TRUY_CAP -> {
                    i.selected = workspaceGroupData.logEnabled!!
                }
                SetupConfig.CHONG_MA_DOC_TAN_CONG_MANG -> {
                    i.selected = workspaceGroupData.malwareEnabled!!
                }
            }
        }
        configAdapter?.addData(a)
    }
}