package com.vn.visafe_android.function.forgotpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {

    lateinit var viewBinding: ActivityForgotPasswordBinding
    private var rootId: Int = R.id.fragment_forgot_password_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        handlerFragment(InputEmailFragment(), tag = "InputEmailFragment")
    }

    fun handlerFragment(fragment: Fragment, tag: String) {
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped: Boolean = manager.popBackStackImmediate(tag, 0)
        if (!fragmentPopped) { //fragment not in back stack, create it.
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_out_right, R.anim.slide_in_right
            )
            ft.replace(rootId, fragment)
            ft.addToBackStack(tag)
            ft.commit()
        }

    }

}