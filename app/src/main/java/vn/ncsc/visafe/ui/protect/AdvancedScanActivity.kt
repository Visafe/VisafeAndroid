package vn.ncsc.visafe.ui.protect

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityAdvancedScanBinding
import vn.ncsc.visafe.model.response.BotnetResponse
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper

class AdvancedScanActivity : BaseActivity() {

    companion object {
        const val TYPE_PROTECT_DEVICE = "TYPE_PROTECT_DEVICE"
        const val TYPE_PROTECT_WIFI = "TYPE_PROTECT_WIFI"
        const val TYPE_BLOCK_ADS = "TYPE_BLOCK_ADS"
        const val TYPE_BLOCK_TRACKING = "TYPE_BLOCK_TRACKING"
    }

    lateinit var binding: ActivityAdvancedScanBinding
    private var isScan = false
    private var countdownTimer: CountDownTimer? = null
    private var botnet: BotnetResponse? = null
    private var percent = 0f
    private var countSuccess = 0
    private var listError: MutableList<String> = mutableListOf()
    private var scanDeviceAdapter: ScanDeviceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvancedScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        scanDeviceAdapter = ScanDeviceAdapter(listError)
        binding.layoutScanSuccess.rcvScan.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.layoutScanSuccess.rcvScan.adapter = scanDeviceAdapter
        binding.btnScan.setOnClickListener {
            SharePreferenceKeyHelper.getInstance(application)
                .putString(PreferenceKey.TIME_LAST_SCAN, (System.currentTimeMillis() / 1000).toString())
            isScan = !isScan
            if (isScan) {
                binding.btnScan.text = "Dừng"
                binding.layoutScanIntro.ctrlIntro.visibility = View.GONE
                binding.frameContainerScan.visibility = View.VISIBLE
                binding.layoutScanSuccess.ctrlSuccess.visibility = View.GONE
                binding.tabs.visibility = View.VISIBLE
                initScanner()
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.vScan.visibility = View.VISIBLE
                binding.btnScan.text = "Quét"
                binding.layoutScanIntro.ctrlIntro.visibility = View.VISIBLE
                binding.frameContainerScan.visibility = View.GONE
                binding.layoutScanSuccess.ctrlSuccess.visibility = View.GONE
                binding.tabs.visibility = View.GONE
                countdownTimer?.cancel()
                percent = 0f
                countSuccess = 0
                binding.circularProgress.progress = percent
                binding.circularProgress.circleProgressColor =
                    ContextCompat.getColor(applicationContext, R.color.colorPrimary)
            }

        }
    }

    private fun initScanner() {
        listError.clear()
        addFragment(ScanActionFragment.newInstance(TYPE_PROTECT_DEVICE), "")
        onPageSelected(0)
        binding.vScan.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBar.setProgress(100)
        countdownTimer = object : CountDownTimer(18000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                when (updateTime(millisUntilFinished)) {
                    16 -> {
                        if (SharePreferenceKeyHelper.getInstance(application).getBoolean(PreferenceKey.STATUS_OPEN_VPN)) {
                            percent += 25f
                            countSuccess++
                            setProgress()
                            binding.circularProgress.progress = percent
                            binding.circularProgress.circleProgressColor =
                                ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                        } else {
                            listError.add("Chế độ chống lừa đảo, mã độc, tấn công mạng chưa được kích hoạt!")
                        }
                    }
                    12 -> {
                        checkBotnet()
                        addFragment(ScanActionFragment.newInstance(TYPE_PROTECT_WIFI), "")
                        onPageSelected(1)
                    }
                    8 -> {
                        if (isAvailableFingerprint(applicationContext) || doesDeviceHaveSecuritySetup(applicationContext)) {
                            percent += 25f
                            countSuccess++
                            setProgress()
                            binding.circularProgress.progress = percent
                            binding.circularProgress.circleProgressColor =
                                ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                        } else {
                        }
                        addFragment(ScanActionFragment.newInstance(TYPE_BLOCK_ADS), "")
                        onPageSelected(2)
                    }
                    4 -> {
                        if (isApiVersionGraterOrEqual()) {
                            percent += 25f
                            countSuccess++
                            setProgress()
                            binding.circularProgress.progress = percent
                            binding.circularProgress.circleProgressColor =
                                ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                        } else {
                            listError.add("Hệ điều hành của bạn đang ở phiên bản 7.0 đã quá cũ, bạn cần nâng cấp hệ điều hành cho thiết bị!")
                        }
                        addFragment(ScanActionFragment.newInstance(TYPE_BLOCK_TRACKING), "")
                        onPageSelected(3)
                    }
                }
            }

            override fun onFinish() {
                binding.btnScan.text = "Quét"
                binding.layoutScanIntro.ctrlIntro.visibility = View.GONE
                binding.frameContainerScan.visibility = View.GONE
                binding.layoutScanSuccess.ctrlSuccess.visibility = View.VISIBLE
                binding.tabs.visibility = View.GONE
                countdownTimer?.cancel()
                isScan = false
                percent = 0f
                countSuccess = 0
                binding.circularProgress.progress = percent
                binding.circularProgress.circleProgressColor =
                    ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                scanDeviceAdapter?.notifyDataSetChanged()
                binding.vScan.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
        countdownTimer?.start()
    }

    private fun checkBotnet() {
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doCheckBotnet()
        call.enqueue(BaseCallback(this@AdvancedScanActivity, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val buffer = response.body()?.source()?.buffer?.readByteArray()
                    val dataString = buffer?.decodeToString()
                    botnet = Gson().fromJson(dataString, BotnetResponse::class.java)
                    if (botnet?.status == NetworkClient.CODE_SUCCESS && isWPA2()) {
                        percent += 25f
                        countSuccess++
                        setProgress()
                        binding.circularProgress.progress = percent
                        binding.circularProgress.circleProgressColor =
                            ContextCompat.getColor(applicationContext, R.color.colorPrimary)
                    } else {
                        listError.add("Wifi đang sử dụng là wifi không an toàn, vui lòng ngắt kết nối tới wifi này!")
                    }
                } else {
                    listError.add("Wifi đang sử dụng là wifi không an toàn, vui lòng ngắt kết nối tới wifi này!")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
            }
        }))
    }

    private fun updateTime(time_in_milli_seconds: Long): Int {
        return ((time_in_milli_seconds / 1000) % 60).toInt()
    }

    fun addFragment(fragment: Fragment, tag: String = "") {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainerScan, fragment)
            .setCustomAnimations(
                R.anim.slide_in_left_1, R.anim.slide_out_left_1,
                R.anim.slide_out_right_1, R.anim.slide_in_right_1
            )
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                binding.tab1.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                binding.tab2.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab3.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab4.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
            }
            1 -> {
                binding.tab1.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab2.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                binding.tab3.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab4.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
            }
            2 -> {
                binding.tab1.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab2.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab3.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                binding.tab4.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)

            }
            3 -> {
                binding.tab1.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab2.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab3.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                binding.tab4.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
            }
        }

    }

    private fun setProgress() {
        binding.progressBar.setProgress(
            when (countSuccess) {
                1 -> 75
                2 -> 53
                3 -> 25
                else -> 0
            }
        )
    }
}