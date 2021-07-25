package vn.ncsc.visafe.ui.upgrade

import android.os.Bundle
import android.view.View
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityUpgradeBinding
import vn.ncsc.visafe.ui.adapter.ScreenSlidePagerAdapter

class UpgradeActivity : BaseActivity() {
    companion object {
        const val UPGRADE_KEY = "UPGRADE_KEY"
        const val CURRENT_VERSION_KEY = "CURRENT_VERSION_KEY"

        const val TYPE_PREMIUM = "TYPE_PREMIUM"
        const val TYPE_FAMILY = "TYPE_FAMILY"
        const val TYPE_BUSSINESS = "TYPE_BUSSINESS"

        const val TYPE_REGISTER = "TYPE_REGISTER"
        const val TYPE_USED = "TYPE_USED"
    }

    private lateinit var binding: ActivityUpgradeBinding
    private var pagerAdapter: ScreenSlidePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpgradeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)

        val type = intent?.extras?.getString(CURRENT_VERSION_KEY, "") ?: ""
        if (type.isNotEmpty() && type == TYPE_REGISTER) {
            initUpgradeRegister()
        } else {
            initCurrentUsed(TYPE_FAMILY)
        }
    }

    private fun initUpgradeRegister() {
        pagerAdapter?.addFragment(UpgradeVersionFragment.newInstance(TYPE_PREMIUM), TYPE_PREMIUM)
        pagerAdapter?.addFragment(UpgradeVersionFragment.newInstance(TYPE_FAMILY), TYPE_FAMILY)
        pagerAdapter?.addFragment(
            UpgradeVersionFragment.newInstance(TYPE_BUSSINESS),
            TYPE_BUSSINESS
        )
        binding.vpVersion.offscreenPageLimit = 3
        binding.vpVersion.adapter = pagerAdapter
        binding.indicator.setViewPager(binding.vpVersion)
    }

    private fun initCurrentUsed(type: String) {
        pagerAdapter?.addFragment(CurrentVersionFragment.newInstance(type), type)
        binding.vpVersion.offscreenPageLimit = 1
        binding.vpVersion.adapter = pagerAdapter

        binding.indicator.visibility = View.GONE
    }
}