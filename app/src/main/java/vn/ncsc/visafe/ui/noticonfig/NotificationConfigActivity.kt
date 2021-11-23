package vn.ncsc.visafe.ui.noticonfig

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationManagerCompat
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityNotificationConfigBinding
import vn.ncsc.visafe.utils.OnSingleClickListener
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.security.AccessController.getContext


class NotificationConfigActivity : BaseActivity() {

    lateinit var binding: ActivityNotificationConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkNotificationPermission()
        initView()
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }
    private fun checkNotificationPermission()
    {
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled())
        {
            binding.switchProtect.isChecked = true
        } else {
            binding.switchProtect.isChecked = false
        }
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })

        binding.switchProtect.setOnCheckedChangeListener { _, isChecked ->
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivity(intent)
        }
    }
}