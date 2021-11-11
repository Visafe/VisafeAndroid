package vn.ncsc.visafe.ui.pin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivitySettingPinBinding
import vn.ncsc.visafe.utils.*

class SettingPinActivity : BaseActivity() {

    lateinit var binding: ActivitySettingPinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingPinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        EventUtils.isCreatePass.observe(this, {
            val pinSaved = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE) ?: ""
            binding.tvTitle.text = (if (pinSaved.isEmpty()) "Cài đặt mã bảo vệ" else "Thay đổi mã bảo vệ")
            binding.clDeletePin.visibility = if (pinSaved.isEmpty()) View.GONE else View.VISIBLE
        })
        val pinSaved = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE) ?: ""
        binding.tvTitle.text = (if (pinSaved.isEmpty()) "Cài đặt mã bảo vệ" else "Thay đổi mã bảo vệ")
        binding.clDeletePin.visibility = if (pinSaved.isEmpty()) View.GONE else View.VISIBLE

        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })
        binding.clUpdatePin.setOnSingClickListener {
            startActivity(Intent(this, UpdatePinActivity::class.java))
        }
        binding.clDeletePin.setOnSingClickListener {
            val intent = Intent(this, UpdatePinActivity::class.java)
            intent.putExtra(UpdatePinActivity.TYPE_ACTION, UpdatePinActivity.IS_DELETE_PIN)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val manager: FragmentManager = supportFragmentManager
        if (manager.fragments.isEmpty()) {
            finish()
        }
    }
}