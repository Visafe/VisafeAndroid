package vn.ncsc.visafe.ui.upgrade

import android.os.Bundle
import android.util.Log
import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityUpgradeBinding
import vn.ncsc.visafe.model.response.AllPackageResponse
import vn.ncsc.visafe.ui.adapter.ScreenSlidePagerAdapter

class UpgradeActivity : BaseActivity() {
    companion object {
        const val UPGRADE_KEY = "UPGRADE_KEY"
        const val CURRENT_PACKAGE = "CURRENT_VERSION_KEY"
        const val DATA_PACKAGE = "DATA_PACKAGE"

        const val TYPE_PREMIUM = "PREMIUM"
        const val TYPE_FAMILY = "FAMILY"
        const val TYPE_BUSINESS = "BUSINESS"

        const val TYPE_REGISTER = "TYPE_REGISTER"
        const val TYPE_USED = "TYPE_USED"
    }

    private lateinit var binding: ActivityUpgradeBinding
    private var pagerAdapter: ScreenSlidePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpgradeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val type = intent?.extras?.getString(CURRENT_PACKAGE, "") ?: ""
        initView()
        getAllPackage(type)
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initUpgradeRegister(response: MutableList<AllPackageResponse>) {
        for (data in response.iterator()) {
            when (data.name) {
                TYPE_PREMIUM -> pagerAdapter?.addFragment(UpgradeVersionFragment.newInstance(TYPE_PREMIUM, data), TYPE_PREMIUM)
                TYPE_FAMILY -> pagerAdapter?.addFragment(UpgradeVersionFragment.newInstance(TYPE_FAMILY, data), TYPE_FAMILY)
                TYPE_BUSINESS -> pagerAdapter?.addFragment(
                    UpgradeVersionFragment.newInstance(TYPE_BUSINESS, data),
                    TYPE_BUSINESS
                )
            }
        }
        binding.vpVersion.offscreenPageLimit = 3
        binding.vpVersion.adapter = pagerAdapter
        binding.indicator.setViewPager(binding.vpVersion)
    }

    private fun initCurrentUsed(type: String, response: MutableList<AllPackageResponse>) {
        for (data in response.iterator()) {
            when (data.name) {
                type -> pagerAdapter?.addFragment(CurrentVersionFragment.newInstance(TYPE_PREMIUM, data), TYPE_PREMIUM)
            }
        }
        binding.vpVersion.offscreenPageLimit = 1
        binding.vpVersion.adapter = pagerAdapter
        binding.indicator.visibility = View.GONE
    }

    private fun getAllPackage(type: String) {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doGetAllPackage()
        call.enqueue(BaseCallback(this, object : Callback<MutableList<AllPackageResponse>> {
            override fun onResponse(
                call: Call<MutableList<AllPackageResponse>>,
                response: Response<MutableList<AllPackageResponse>>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.let {
                        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
                        if (type.isNotEmpty() && (type == TYPE_REGISTER || type == "PERSONAL")) {
                            initUpgradeRegister(it)
                        } else {
                            initCurrentUsed(type, it)
                        }
                    }
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<MutableList<AllPackageResponse>>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

}