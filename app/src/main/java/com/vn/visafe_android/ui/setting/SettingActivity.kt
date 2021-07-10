package com.vn.visafe_android.ui.setting

import android.os.Bundle
import android.view.View
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityMainBinding
import com.vn.visafe_android.databinding.ActivitySettingBinding
import com.vn.visafe_android.utils.OnSingleClickListener

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
    }
}