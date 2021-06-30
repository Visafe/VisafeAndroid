package com.vn.visafe_android.ui.home

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentProtectBinding

class ProtectFragment : BaseFragment<FragmentProtectBinding>() {

    companion object {
        fun newInstance() = ProtectFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_protect

    override fun initView() {

    }

}