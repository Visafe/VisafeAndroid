package com.vn.visafe_android.ui.home.administrator

import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentConfigruationBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.ui.adapter.ConfigAdapter
import com.vn.visafe_android.ui.adapter.DeviceMostAdapter
import com.vn.visafe_android.ui.adapter.GroupListAdapter
import com.vn.visafe_android.utils.SetupConfig

class ConfigruationFragment : BaseFragment<FragmentConfigruationBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            ConfigruationFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_configruation

    override fun initView() {
        val configAdapter = ConfigAdapter()
        configAdapter.onChangeConfig = object : ConfigAdapter.OnChangeConfig {
            override fun onChangeConfig(data: SetupConfig) {

            }

        }
        binding.rvConfig.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvConfig.adapter = configAdapter
    }
}