package vn.ncsc.visafe.ui.website

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityWebsiteReportBinding
import vn.ncsc.visafe.model.UserInfo
import vn.ncsc.visafe.model.request.ReportWebRequest
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
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
            reportWebsite()
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

    private fun reportWebsite() {
        showProgressDialog()
        val reportWebRequest = ReportWebRequest(url = binding.editWebsite.text.toString())
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doReportWebsitePhishing(reportWebRequest)
        call.enqueue(BaseCallback(this@WebsiteReportActivity, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val dialog = SuccessDialogFragment.newInstance(
                        "Gửi báo cáo thành công!", "Thông tin báo cáo của bạn đã được ViSafe tiếp nhận.", "Đã hiểu"
                    )
                    dialog.show(supportFragmentManager, "")
                    dialog.setOnClickListener {
                        when (it) {
                            Action.CONFIRM -> {
                                finish()
                            }
                            else -> {
                                return@setOnClickListener
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}