package vn.ncsc.visafe.ui.protect

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityProtectWifiBinding
import vn.ncsc.visafe.model.ProtectWifiData
import vn.ncsc.visafe.ui.adapter.OnClickWifi
import vn.ncsc.visafe.ui.adapter.ProtectWifiAdapter
import vn.ncsc.visafe.utils.OnSingleClickListener

class ProtectWifiActivity : BaseActivity() {
    companion object {
        const val PROTECT_WIFI_KEY = "PROTECT_WIFI_KEY"
    }

    lateinit var binding: ActivityProtectWifiBinding
    private lateinit var adapter: ProtectWifiAdapter

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

        val isProtected = intent.extras?.getBoolean(PROTECT_WIFI_KEY, false) ?: false
        binding.switchProtectWifi.isChecked = isProtected
        handleProtected(isProtected)
        binding.switchProtectWifi.setOnCheckedChangeListener { compoundButton, isChecked ->
            binding.switchProtectWifi.isChecked = isChecked
            handleProtected(isChecked)
        }

        adapter = ProtectWifiAdapter(createWifiList(), this)
        binding.rvData.adapter = adapter
        adapter.setOnClickListener(object : OnClickWifi {
            override fun onClickWifi(data: ProtectWifiData, position: Int) {

            }

            override fun onMoreWifi(data: ProtectWifiData, position: Int) {
//                showDialogEdit(data, position)
            }

        })
    }

    private fun handleProtected(isProtected: Boolean) {
        if (isProtected) {
            binding.llProtected.visibility = View.VISIBLE
            binding.llNoProtect.visibility = View.GONE
            binding.ivCheck.setImageResource(R.drawable.ic_checkmark_circle)
            binding.ivWifi.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.protected_wifi),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        } else {
            binding.llProtected.visibility = View.GONE
            binding.llNoProtect.visibility = View.VISIBLE
            binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
            binding.ivWifi.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.no_protected_wifi),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    private fun createWifiList(): ArrayList<ProtectWifiData> {
        val list: ArrayList<ProtectWifiData> = ArrayList()
        list.add(ProtectWifiData("https://www.facebook.com/", 12, true))
        list.add(ProtectWifiData("https://www.facebook.com/", 15, false))
        list.add(ProtectWifiData("https://www.facebook.com/", 7, true))
        list.add(ProtectWifiData("https://www.facebook.com/", 19, false))
        list.add(ProtectWifiData("https://www.facebook.com/", 54, false))
        return list
    }

}