package com.vn.visafe_android.ui.support

import android.os.Bundle
import android.view.View
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivitySupportCenterBinding
import com.vn.visafe_android.utils.OnSingleClickListener
import com.vn.visafe_android.utils.Utils.callPhone
import com.vn.visafe_android.utils.Utils.openWebsite
import com.vn.visafe_android.utils.Utils.sendEmail
import com.vn.visafe_android.utils.setOnSingClickListener

class SupportCenterActivity : BaseActivity() {

    lateinit var binding: ActivitySupportCenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })
        binding.clHotlineSupport.setOnSingClickListener {
            callPhone(getString(R.string.hotline), this)
        }
        binding.clEmail.setOnSingClickListener {
            sendEmail(this, getString(R.string.email_support), "", "")
        }
        binding.clMessenger.setOnSingClickListener {
            openWebsite("https://www.facebook.com/govSOC", this)
        }
        binding.clFacebook.setOnSingClickListener {
            openWebsite("https://www.facebook.com/govSOC", this)
        }
    }
}