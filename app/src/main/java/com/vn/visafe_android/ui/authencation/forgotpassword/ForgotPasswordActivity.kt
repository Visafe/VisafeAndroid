package com.vn.visafe_android.ui.authencation.forgotpassword

import android.os.Bundle
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {

    lateinit var viewBinding: ActivityForgotPasswordBinding

    companion object {
        var rootId: Int = R.id.fragment_forgot_password_container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        handlerFragment(InputEmailFragment(), rootId, tag = "InputEmailFragment")
    }

}