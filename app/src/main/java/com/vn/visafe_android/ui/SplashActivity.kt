package com.vn.visafe_android.ui

import android.content.Intent
import android.os.Bundle
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.ui.authentication.LoginActivity
import com.vn.visafe_android.utils.SharePreferenceKeyHelper

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SharePreferenceKeyHelper.getInstance(application).isLogin()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}