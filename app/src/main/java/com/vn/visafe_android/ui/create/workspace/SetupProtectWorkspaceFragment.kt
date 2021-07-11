package com.vn.visafe_android.ui.create.workspace

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentSetupProtectWorkspaceBinding
import com.vn.visafe_android.ui.adapter.ConfigAdapter
import com.vn.visafe_android.utils.SetupConfig
import com.vn.visafe_android.utils.setOnSingClickListener

class SetupProtectWorkspaceFragment : BaseFragment<FragmentSetupProtectWorkspaceBinding>() {

    companion object {
        fun newInstance(): SetupProtectWorkspaceFragment {
            val args = Bundle()
            val fragment = SetupProtectWorkspaceFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var createWorkspaceActivity: CreateWorkspaceActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateWorkspaceActivity) {
            createWorkspaceActivity = context
        }
    }

    override fun layoutRes(): Int = R.layout.fragment_setup_protect_workspace

    var setupConfig: SetupConfig? = null
    override fun initView() {
        val configAdapter = ConfigAdapter()
        configAdapter.onChangeConfig = object : ConfigAdapter.OnChangeConfig {
            override fun onChangeConfig(data: SetupConfig) {
                when (data) {
                    SetupConfig.CHONG_LUA_DAO_MANG -> {
                        createWorkspaceActivity?.createWorkSpaceRequest?.phishingEnabled = data.selected
                    }
                    SetupConfig.LUU_LICH_SU_TRUY_CAP -> {
                        createWorkspaceActivity?.createWorkSpaceRequest?.logEnabled = data.selected
                    }
                    SetupConfig.CHONG_MA_DOC_TAN_CONG_MANG -> {
                        createWorkspaceActivity?.createWorkSpaceRequest?.malwareEnabled = data.selected
                    }
                }
            }

        }
        binding.rvConfig.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvConfig.adapter = configAdapter
        binding.tvNext.setOnSingClickListener {
            createWorkspaceActivity?.doCreateGroup()
        }
    }
}