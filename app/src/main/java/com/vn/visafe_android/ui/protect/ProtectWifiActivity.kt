package com.vn.visafe_android.ui.protect

import android.os.Bundle
import android.view.View
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityProtectWifiBinding
import com.vn.visafe_android.utils.OnSingleClickListener

class ProtectWifiActivity : BaseActivity() {
    companion object {
        const val PROTECT_WIFI_KEY = "PROTECT_WIFI_KEY"
    }

    lateinit var binding: ActivityProtectWifiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProtectWifiBinding.inflate(layoutInflater)
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