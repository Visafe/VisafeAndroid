package com.vn.visafe_android.ui.pin

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityForgotPasswordBinding
import com.vn.visafe_android.databinding.ActivityUpdatePinBinding
import com.vn.visafe_android.ui.authentication.forgotpassword.ForgotPasswordActivity
import com.vn.visafe_android.ui.authentication.forgotpassword.InputEmailFragment

class UpdatePinActivity : BaseActivity() {

    lateinit var binding: ActivityUpdatePinBinding

    companion object {
        var rootId: Int = R.id.fragment_pin
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        handlerFragment(CurrentPinFragment(), rootId, tag = "CurrentPinFragment")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val manager: FragmentManager = supportFragmentManager
        if (manager.fragments.isEmpty()) {
            finish()
        }
    }
}