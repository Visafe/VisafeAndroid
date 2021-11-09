package vn.ncsc.visafe.ui.noticonfig

import android.os.Bundle
import android.view.View
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityNotificationConfigBinding
import vn.ncsc.visafe.utils.OnSingleClickListener

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