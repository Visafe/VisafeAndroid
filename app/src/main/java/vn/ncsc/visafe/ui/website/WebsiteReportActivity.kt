package vn.ncsc.visafe.ui.website

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityWebsiteReportBinding
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class WebsiteReportActivity : BaseActivity() {
    private lateinit var binding: ActivityWebsiteReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebsiteReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })
        enableButton()
        binding.editWebsite.addTextChangedListener {
            enableButton()
        }
        binding.tvSend.setOnSingClickListener {
            val dialog = SuccessDialogFragment.newInstance(
                    "Gửi báo cáo thành công!", "Thông tin báo cáo của bạn đã được ViSafe tiếp nhận.", "Đã hiểu")
            dialog.show(supportFragmentManager, "")
            dialog.setOnClickListener {
                when (it) {
                    Action.CONFIRM -> {
                        setResult(RESULT_OK)
                        finish()
                    }
                    else -> {
                        return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun enableButton() {
        val website = binding.editWebsite.text.toString()
        if (website.isNotBlank()) {
            with(binding.tvSend) {
                backgroundTintList =
                    resources.getColorStateList(R.color.color_FFB31F, theme)

                setTextColor(ContextCompat.getColor(this@WebsiteReportActivity, R.color.white))
                isEnabled = true
            }
        } else {
            with(binding.tvSend) {
                backgroundTintList =
                    resources.getColorStateList(R.color.color_F8F8F8, theme)
                setTextColor(ContextCompat.getColor(this@WebsiteReportActivity, R.color.color_AAAAAA))
                isEnabled = false
            }
        }
    }
}