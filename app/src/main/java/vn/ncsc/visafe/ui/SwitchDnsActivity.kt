package vn.ncsc.visafe.ui

import android.R.attr.x
import android.R.attr.y
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_switch_dns.*
import vn.ncsc.visafe.R
import vn.ncsc.visafe.dns.sys.PersistentState
import vn.ncsc.visafe.dns.sys.VpnController
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import java.util.*


class SwitchDnsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_dns)
        initView()
        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                when (checkedId) {
                    R.id.radio0 -> {
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.HOST_NAME,
                                "https://security.visafe.vn/dns-query/"
                            )
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.RADIO_BUTTON_DNS,
                                "0"
                            )
                    }
                    R.id.radio1 -> {
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.HOST_NAME,
                                "https://family.visafe.vn/dns-query/"
                            )
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.RADIO_BUTTON_DNS,
                                "1"
                            )
                    }
                    R.id.radio2 -> {
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.HOST_NAME,
                                "https://securityplus.visafe.vn/dns-query/"
                            )
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.RADIO_BUTTON_DNS,
                                "2"
                            )
                    }
                    R.id.radio3 -> {
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.HOST_NAME,
                                "https://custom.visafe.vn/dns-query/"
                            )
                        SharePreferenceKeyHelper.getInstance(application)
                            .putString(
                                PreferenceKey.RADIO_BUTTON_DNS,
                                "3"
                            )
                    }
                    else -> {
                    }

                }
                startDnsVpnService()
            })

        iv_back.setOnClickListener {
            finish()
        }
    }
    private fun startDnsVpnService() {
        applicationContext?.let { VpnController.instance.start(it) }
    }

    private fun initView()
    {
        val result:String =  SharePreferenceKeyHelper.getInstance(application)
            .getString(
                PreferenceKey.RADIO_BUTTON_DNS
            )
        when (result) {
            "0" -> {
                radio0.isChecked = true
            }
            "1" -> {
                radio1.isChecked = true
            }
            "2" -> {
                radio2.isChecked = true
            }
            else -> {
                radio3.isChecked = true
            }
        }
    }
}