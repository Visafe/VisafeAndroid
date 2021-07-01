package com.vn.visafe_android.ui.home.administrator

import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentConfigruationBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.adapter.ConfigAdapter
import com.vn.visafe_android.utils.SetupConfig

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