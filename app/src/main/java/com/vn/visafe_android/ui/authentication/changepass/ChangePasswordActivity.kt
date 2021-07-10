package com.vn.visafe_android.ui.authentication.changepass

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : BaseActivity() {

    lateinit var binding: ActivityChangePasswordBinding

    companion object {
        var rootId: Int = R.id.fragment_pass
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        handlerFragment(CurrentPassFragment(), rootId, tag = "CurrentPassFragment")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val manager: FragmentManager = supportFragmentManager
        if (manager.fragments.isEmpty()) {
            finish()
        }
    }
}