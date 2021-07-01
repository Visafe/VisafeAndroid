package com.vn.visafe_android.ui.home

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentUtilitiesHomeBinding

class UtilitiesHomeFragment : BaseFragment<FragmentUtilitiesHomeBinding>() {
    companion object {
        fun newInstance() = UtilitiesHomeFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_utilities_home

    override fun initView() {

    }

}