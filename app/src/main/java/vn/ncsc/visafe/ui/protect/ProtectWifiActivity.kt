package vn.ncsc.visafe.ui.protect

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityProtectWifiBinding
import vn.ncsc.visafe.model.DetailBotnet
import vn.ncsc.visafe.model.response.BotnetResponse
import vn.ncsc.visafe.ui.adapter.OnClickWifi
import vn.ncsc.visafe.ui.adapter.ProtectWifiAdapter
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper

class ProtectWifiActivity : BaseActivity() {
    companion object {
        const val PROTECT_WIFI_KEY = "PROTECT_WIFI_KEY"
    }

    lateinit var binding: ActivityProtectWifiBinding
    private lateinit var adapter: ProtectWifiAdapter
    private var listBotnetDetail: ArrayList<DetailBotnet> = arrayListOf()

    var botnetResponse: BotnetResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProtectWifiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK)
        finish()
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                setResult(RESULT_OK)
                finish()
            }
        })
        val isProtected = intent.extras?.getBoolean(PROTECT_WIFI_KEY, false) ?: false
        if (isProtected) {
            checkBotnet()
        } else {
            binding.switchProtectWifi.isChecked = false
            handleProtected(false, null)
        }
        binding.switchProtectWifi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBotnet()
            } else {
                binding.switchProtectWifi.isChecked = false
                handleProtected(false, null)
            }
            SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.IS_ENABLE_PROTECTED_WIFI_HOME, isChecked)
        }

        adapter = ProtectWifiAdapter(listBotnetDetail, this)
        binding.rvData.adapter = adapter
        adapter.setOnClickListener(object : OnClickWifi {
            override fun onClickWifi(data: DetailBotnet, position: Int) {
            }

            override fun onMoreWifi(data: DetailBotnet, position: Int) {
            }

        })
    }

    private fun checkBotnet() {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doCheckBotnet()
        call.enqueue(BaseCallback(this@ProtectWifiActivity, object : Callback<BotnetResponse> {
            override fun onResponse(
                call: Call<BotnetResponse>,
                response: Response<BotnetResponse>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val data = response.body()
                    botnetResponse = data
                    handleProtected(true, botnetResponse)
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<BotnetResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun handleProtected(isProtected: Boolean, botnet: BotnetResponse?) {
        val ipAddress = getIpAddress()
        if (isProtected) {
            if (botnet != null) {
                binding.ivCheck.setImageResource(R.drawable.ic_checkmark_circle)
                binding.ivWifi.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
                binding.tvTitle.text = HtmlCompat.fromHtml(
                    getString(R.string.protected_wifi),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.tvDescription.text = "Địa chỉ IP: $ipAddress"
            } else {
                binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
                binding.ivWifi.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
                binding.tvTitle.text = HtmlCompat.fromHtml(
                    getString(R.string.protected_wifi_not_safe),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.tvDescription.text = "Địa chỉ IP: $ipAddress"
            }

            if (botnet?.detail?.size == 0) {
                binding.llProtected.visibility = View.GONE
                binding.llNoProtect.visibility = View.VISIBLE
                binding.tvNotification.text = "Không có mã độc nào được phát hiện"
                binding.tvNote.visibility = View.GONE
                binding.ivError.visibility = View.GONE
            } else {
                listBotnetDetail.clear()
                botnet?.detail?.let { listBotnetDetail.addAll(it) }
                adapter.notifyDataSetChanged()
                binding.llProtected.visibility = View.VISIBLE
                binding.llNoProtect.visibility = View.GONE
            }

        } else {
            binding.tvDescription.text = ""
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
        binding.switchProtectWifi.isChecked = isProtected
    }
}