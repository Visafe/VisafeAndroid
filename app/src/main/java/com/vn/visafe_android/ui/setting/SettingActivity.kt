package com.vn.visafe_android.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivitySettingBinding
import com.vn.visafe_android.ui.authentication.changepass.ChangePasswordActivity
import com.vn.visafe_android.ui.noti.NotificationConfigActivity
import com.vn.visafe_android.ui.pin.UpdatePinActivity
import com.vn.visafe_android.utils.OnSingleClickListener
import com.vn.visafe_android.utils.setOnSingClickListener

class SettingActivity : BaseActivity() {

    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }

        })
        binding.clUpdatePin.setOnSingClickListener {
            startActivity(Intent(this, UpdatePinActivity::class.java))
        }
        binding.clNoti.setOnSingClickListener {
            startActivity(Intent(this, NotificationConfigActivity::class.java))
        }
        binding.clChangePass.setOnSingClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }
}