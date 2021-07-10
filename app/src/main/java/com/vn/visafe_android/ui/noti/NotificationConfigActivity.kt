package com.vn.visafe_android.ui.noti

import android.os.Bundle
import android.view.View
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityNotificationConfigBinding
import com.vn.visafe_android.utils.OnSingleClickListener

class NotificationConfigActivity : BaseActivity() {

    lateinit var binding: ActivityNotificationConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })
    }
}