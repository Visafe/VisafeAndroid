package com.vn.visafe_android.ui.create.workspace

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentSetupProtectWorkspaceBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.adapter.ConfigAdapter
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.create.workspace.dialog.DeleteWorkspaceBottomSheet
import com.vn.visafe_android.ui.create.workspace.dialog.DialogCreateSuccessWorkSpace
import com.vn.visafe_android.utils.SetupConfig

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

    override fun initView() {
        val configAdapter = ConfigAdapter()
        configAdapter.onChangeConfig = object : ConfigAdapter.OnChangeConfig {
            override fun onChangeConfig(data: SetupConfig) {

            }

        }
        binding.rvConfig.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvConfig.adapter = configAdapter
        binding.tvNext.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val bottomSheet = DialogCreateSuccessWorkSpace()
        bottomSheet.isCancelable = false
        bottomSheet.show(createWorkspaceActivity?.supportFragmentManager!!, null)
        bottomSheet.setOnClickListener {
            when (it) {
                Action.CONFIRM -> {
                    createWorkspaceActivity?.finish()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

}