package com.vn.visafe_android.ui.home.administrator

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentConfigruationBinding

class ConfigruationFragment : BaseFragment<FragmentConfigruationBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            ConfigruationFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_configruation

    override fun initView() {
    }
}