package com.vn.visafe_android.ui.home

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentProfileBinding
import com.vn.visafe_android.ui.MainActivity

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_profile

    override fun initView() {
        (activity as MainActivity).user.observe(this, {
            if (it != null) {
                binding.tvName.text = it.fullName
                binding.tvPhone.text = it.phoneNumber
            }
        })
    }
}