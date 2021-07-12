package vn.ncsc.visafe.ui.support

import android.os.Bundle
import android.view.View
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivitySupportCenterBinding
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.Utils.callPhone
import vn.ncsc.visafe.utils.Utils.openWebsite
import vn.ncsc.visafe.utils.Utils.sendEmail
import vn.ncsc.visafe.utils.setOnSingClickListener

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
            sendEmail(this, getString(R.string.email_support), "")
        }
        binding.clMessenger.setOnSingClickListener {
            openWebsite("https://www.facebook.com/govSOC", this)
        }
        binding.clFacebook.setOnSingClickListener {
            openWebsite("https://www.facebook.com/govSOC", this)
        }
    }
}