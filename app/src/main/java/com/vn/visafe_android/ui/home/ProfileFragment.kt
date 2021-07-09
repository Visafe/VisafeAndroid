package com.vn.visafe_android.ui.home

import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.databinding.FragmentProfileBinding
import com.vn.visafe_android.model.UserInfo

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private var userInfo: UserInfo? = null

    override fun layoutRes(): Int = R.layout.fragment_profile

    override fun initView() {
    }

    fun setUserProfile(userInfo: UserInfo?) {
        this.userInfo = userInfo
        if (userInfo != null) {
            binding.tvName.text = userInfo.fullName
            binding.tvPhone.text = userInfo.phoneNumber
        }
    }
}