package vn.ncsc.visafe.ui.authentication.splash

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.facebook.share.Share
import com.squareup.okhttp.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivitySplashBinding
import vn.ncsc.visafe.dns.net.setting.RandomString
import vn.ncsc.visafe.model.RoutingResponse
import vn.ncsc.visafe.model.request.LoginSocialRequest
import vn.ncsc.visafe.model.response.DeviceIdResponse
import vn.ncsc.visafe.model.response.LoginResponse
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.adapter.SectionsPagerAdapter
import vn.ncsc.visafe.ui.group.join.JoinGroupActivity
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import java.io.IOException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.security.cert.CertificateException


class SplashActivity : BaseActivity() {

    lateinit var viewBinding: ActivitySplashBinding
    private var data: Uri? = null
    private var action: String? = null
    private var groupId = ""
    private var groupName = ""
    private var isLoadUserInfo = false

    companion object {
        const val LOAD_USER_INFO = "LOAD_USER_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        intent?.let {
            action = it.action
            data = it.data
            Log.e("SplashActivity: ", data.toString())
        }
        handleDeeplink()
        getDeviceId()
//        val client = NetworkClient()
//        val call = client.clientWithoutToken(context = applicationContext).doGetDnsUrl()
//        call.enqueue(object : Callback<RoutingResponse> {
//            override fun onResponse(call: Call<RoutingResponse>, response: Response<RoutingResponse>) {
//                if (response.code() == NetworkClient.CODE_SUCCESS) {
//                    response.body()?.let {
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
//                    }
//                } else {
//                    SharePreferenceKeyHelper.getInstance(application)
//                        .putString(
//                            PreferenceKey.HOST_NAME,
//                            NetworkClient.DOMAIN
//                        )
//                }
                handleProcessing()
//            }
//
//            override fun onFailure(call: Call<RoutingResponse>, t: Throwable) {
//                SharePreferenceKeyHelper.getInstance(application)
//                    .putString(
//                        PreferenceKey.HOST_NAME,
//                        NetworkClient.DOMAIN
//                    )
//                handleProcessing()
//            }

//        })
    }

    private fun handleProcessing() {
        if (SharePreferenceKeyHelper.getInstance(application).isLogin()
            || !SharePreferenceKeyHelper.getInstance(application).isFirstShowOnBoarding()
        ) {
            viewBinding.imgLogo.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.putExtra(JoinGroupActivity.GROUP_ID, groupId)
                intent.putExtra(JoinGroupActivity.GROUP_NAME, groupName)
                intent.putExtra(LOAD_USER_INFO, isLoadUserInfo)
                startActivity(intent)
                finish()
            }, 2000)
        } else {
            viewBinding.imgLogo.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                initView()
            }, 2000)
        }
    }

    private fun handleDeeplink() {
        if (data != null && data.toString().contains("group/invite/device?")) {
            val subStringUrl =
                data.toString().substring(0, data.toString().indexOf("group/invite/device?") + "group/invite/device?".length)
            val dataString = data.toString().replace(subStringUrl, "")
            val items: Array<String> = dataString.split("&".toRegex()).toTypedArray()
            try {
                for (item in items) {
                    val parted = item.split("=".toRegex(), 2).toTypedArray()
                    if (parted.size < 2 || "" == parted[1].trim { it <= ' ' }) continue
                    val key = parted[0]
                    val value = parted[1]
                    when (key) {
                        "groupId" -> groupId = value
                        "groupName" -> groupName = value
                    }
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("convertData: ", it) }
            }
        } else if (data != null && data.toString().contains("paymentsuccess")) {
            isLoadUserInfo = true
        }
    }

    private fun initView() {
        viewBinding.imgLogo.visibility = View.GONE
        viewBinding.tabs.visibility = View.VISIBLE
        viewBinding.fab.visibility = View.VISIBLE
        viewBinding.viewPager.visibility = View.VISIBLE
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewBinding.viewPager.adapter = sectionsPagerAdapter

        viewBinding.fab.setOnClickListener {
            if (viewBinding.fab.text.equals(getString(R.string.start))) {
                SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.IS_FIRST_SHOW_ON_BOARDING, false)
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.putExtra(JoinGroupActivity.GROUP_ID, groupId)
                intent.putExtra(JoinGroupActivity.GROUP_NAME, groupName)
                intent.putExtra(LOAD_USER_INFO, isLoadUserInfo)
                startActivity(intent)
                finish()
            } else {
                val currentItem = viewBinding.viewPager.currentItem
                viewBinding.viewPager.currentItem = currentItem + 1
            }
        }

        viewBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        viewBinding.tab1.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                        viewBinding.tab2.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab3.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.fab.text = getString(R.string.next)
                    }
                    1 -> {
                        viewBinding.tab1.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab2.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                        viewBinding.tab3.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.fab.text = getString(R.string.next)
                    }
                    2 -> {
                        viewBinding.tab1.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab2.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab3.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                        viewBinding.fab.text = getString(R.string.start)

                    }

                    else -> { // Note the block
                        0
                    }
                }

            }

        })
    }

    fun getDeviceId() {
        if (SharePreferenceKeyHelper.getInstance(application).getString(PreferenceKey.DEVICE_ID).isEmpty()) {
            val client = NetworkClient()
            val call = client.clientWithoutToken(context = applicationContext).doGetDeviceId()
            call.enqueue(BaseCallback(this, object : Callback<DeviceIdResponse> {
                override fun onResponse(
                    call: Call<DeviceIdResponse>,
                    response: Response<DeviceIdResponse>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        response.body()?.deviceId?.let {
                            SharePreferenceKeyHelper.getInstance(application)
                                .putString(PreferenceKey.DEVICE_ID, it.lowercase())
                        }
                    }
                }

                override fun onFailure(call: Call<DeviceIdResponse>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                }
            }))
        }
    }
}