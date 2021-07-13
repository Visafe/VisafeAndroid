package vn.ncsc.visafe.ui.authentication.forgotpassword

import android.os.Bundle
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityForgotPasswordBinding

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