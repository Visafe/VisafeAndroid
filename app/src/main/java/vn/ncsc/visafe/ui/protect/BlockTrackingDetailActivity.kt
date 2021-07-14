package vn.ncsc.visafe.ui.protect

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityBlockTrackingDetailBinding
import vn.ncsc.visafe.model.BlockAdsData
import vn.ncsc.visafe.model.BlockTrackingData
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.ui.adapter.BlockAdsAdapter
import vn.ncsc.visafe.ui.adapter.BlockTrackingAdapter
import vn.ncsc.visafe.ui.adapter.OnClickBlockAds
import vn.ncsc.visafe.ui.adapter.OnClickBlockTracking
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class BlockTrackingDetailActivity : BaseActivity() {
    companion object {
        const val BLOCK_TRACKING_KEY = "BLOCK_TRACKING_KEY"
    }

    lateinit var binding: ActivityBlockTrackingDetailBinding
    private lateinit var adapter: BlockTrackingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockTrackingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbar.setOnClickLeftButton(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                finish()
            }
        })
        val isProtected = intent.extras?.getBoolean(BLOCK_TRACKING_KEY, false) ?: false
        binding.switchBlockTracking.isChecked = isProtected
        handleProtected(isProtected, false)
        binding.switchBlockTracking.setOnCheckedChangeListener { compoundButton, isChecked ->
            binding.switchBlockTracking.isChecked = isChecked
            handleProtected(isChecked, binding.clSetupBlock.visibility == View.VISIBLE)
        }

        adapter = BlockTrackingAdapter(createBlockTracking(), this)
        binding.rvData.adapter = adapter
        adapter.setOnClickListener(object : OnClickBlockTracking {
            override fun onClickBlockTracking(data: BlockTrackingData, position: Int) {

            }

            override fun onMoreBlockTracking(data: BlockTrackingData, position: Int) {

            }

        })

        initSetupBlock()
        binding.btnBlockedTracking.setOnSingClickListener {
            handleProtected(binding.switchBlockTracking.isChecked, false)
            binding.btnBlockedTracking.alpha = 1f
            binding.viewBlock.visibility = View.VISIBLE
            binding.btnSetupBlock.alpha = 0.5f
            binding.viewSetupBlock.visibility = View.INVISIBLE
        }
        binding.btnSetupBlock.setOnSingClickListener {
            binding.clSetupBlock.visibility = View.VISIBLE
            binding.llProtected.visibility = View.GONE
            binding.llNoProtect.visibility = View.GONE
            binding.btnBlockedTracking.alpha = 0.5f
            binding.viewBlock.visibility = View.INVISIBLE
            binding.btnSetupBlock.alpha = 1f
            binding.viewSetupBlock.visibility = View.VISIBLE
        }
    }

    private fun handleProtected(isProtected: Boolean, enableSetupBlock: Boolean) {
        if (isProtected) {
            binding.llProtected.visibility = if (enableSetupBlock) {
                View.GONE
            } else {
                View.VISIBLE
            }
            binding.llNoProtect.visibility = View.GONE
            binding.ivCheck.setImageResource(R.drawable.ic_checkmark_circle)
            binding.ivBlockTracking.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.protected_tracking),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.clSetupBlock.visibility = if (enableSetupBlock) {
                View.VISIBLE
            } else {
                View.GONE
            }
        } else {
            binding.llProtected.visibility = View.GONE
            binding.llNoProtect.visibility = if (enableSetupBlock) {
                View.GONE
            } else {
                View.VISIBLE
            }
            binding.ivCheck.setImageResource(R.drawable.ic_info_circle)
            binding.ivBlockTracking.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.no_protected_tracking),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.clSetupBlock.visibility = if (enableSetupBlock) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun createBlockTracking(): ArrayList<BlockTrackingData> {
        val list: ArrayList<BlockTrackingData> = ArrayList()
        list.add(BlockTrackingData("https://www.facebook.com/", 12, true))
        list.add(BlockTrackingData("https://www.facebook.com/", 15, false))
        list.add(BlockTrackingData("https://www.facebook.com/", 7, true))
        list.add(BlockTrackingData("https://www.facebook.com/", 19, false))
        list.add(BlockTrackingData("https://www.facebook.com/", 54, false))
        return list
    }

    private fun initSetupBlock() {
        binding.itemBlockTrackingDevice.setData(
            arrayListOf(
                Subject(
                    "Alexa",
                    "alexa",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Apple",
                    "apple",
                    R.drawable.ic_apple
                ),
                Subject(
                    "Huawei",
                    "huawei",
                    R.drawable.ic_huawei
                ),
                Subject(
                    "Roku",
                    "roku",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Samsung",
                    "samsung",
                    R.drawable.ic_samsung
                ),
                Subject(
                    "Sonos",
                    "sonos",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Windows",
                    "windows",
                    R.drawable.ic_logo_text
                ),
                Subject(
                    "Xiaomi",
                    "xiaomi",
                    R.drawable.ic_xiaomi
                )
            )
        )
        binding.itemBlockTrackingDevice.setExpanded(false)
        binding.itemBlockTrackingDevice.disableExpanded()
    }

}