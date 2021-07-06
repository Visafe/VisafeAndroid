package com.vn.visafe_android.ui.home

import android.content.Intent
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentSettingBinding
import com.vn.visafe_android.ui.group.join.ScanQRJoinGroupActivity
import com.vn.visafe_android.utils.setOnSingClickListener

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_setting

    override fun initView() {
        binding.btnRegisterDevice.setOnSingClickListener {
            startActivity(Intent(requireContext(), ScanQRJoinGroupActivity::class.java))
        }
    }

}