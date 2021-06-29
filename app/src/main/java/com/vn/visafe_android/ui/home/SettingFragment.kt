package com.vn.visafe_android.ui.home

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentSettingBinding

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_setting

    override fun initView() {

    }

}