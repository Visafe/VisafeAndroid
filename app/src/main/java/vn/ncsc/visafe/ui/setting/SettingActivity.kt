package vn.ncsc.visafe.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import vn.ncsc.visafe.ui.noti.NotificationConfigActivity
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivitySettingBinding
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.authentication.changepass.ChangePasswordActivity
import vn.ncsc.visafe.ui.pin.UpdatePinActivity
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.utils.setOnSingClickListener

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
        binding.clLogout.setOnSingClickListener {
            SharePreferenceKeyHelper.getInstance(application).clearAllData()
            startActivity(
                Intent(this, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }
    }
}