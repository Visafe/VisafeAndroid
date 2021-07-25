package vn.ncsc.visafe.ui.protect

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityBlockAdsBinding
import vn.ncsc.visafe.model.BlockAdsData
import vn.ncsc.visafe.model.Subject
import vn.ncsc.visafe.ui.adapter.BlockAdsAdapter
import vn.ncsc.visafe.ui.adapter.OnClickBlockAds
import vn.ncsc.visafe.utils.OnSingleClickListener
import vn.ncsc.visafe.utils.setOnSingClickListener

class BlockAdsActivity : BaseActivity() {
    companion object {
        const val BLOCK_ADS_KEY = "BLOCK_ADS_KEY"
    }

    lateinit var binding: ActivityBlockAdsBinding
    private lateinit var adapter: BlockAdsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockAdsBinding.inflate(layoutInflater)
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
        val isProtected = intent.extras?.getBoolean(BLOCK_ADS_KEY, false) ?: false
        binding.switchBlockAds.isChecked = isProtected
        handleProtected(isProtected, false)
        binding.switchBlockAds.setOnCheckedChangeListener { compoundButton, isChecked ->
            binding.switchBlockAds.isChecked = isChecked
            handleProtected(isChecked, binding.clSetupBlock.visibility == View.VISIBLE)
        }

        adapter = BlockAdsAdapter(createBlockAds(), this)
        binding.rvData.adapter = adapter
        adapter.setOnClickListener(object : OnClickBlockAds {
            override fun onClickBlockAds(data: BlockAdsData, position: Int) {

            }

            override fun onMoreBlockAds(data: BlockAdsData, position: Int) {

            }

        })

        initSetupBlock()
        binding.btnBlockedAds.setOnSingClickListener {
            handleProtected(binding.switchBlockAds.isChecked, false)
            binding.btnBlockedAds.alpha = 1f
            binding.viewBlock.visibility = View.VISIBLE
            binding.btnSetupBlock.alpha = 0.5f
            binding.viewSetupBlock.visibility = View.INVISIBLE
        }
        binding.btnSetupBlock.setOnSingClickListener {
            binding.clSetupBlock.visibility = View.VISIBLE
            binding.llProtected.visibility = View.GONE
            binding.llNoProtect.visibility = View.GONE
            binding.btnBlockedAds.alpha = 0.5f
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
            binding.ivBlockAds.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_green_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.protected_ads),
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
            binding.ivBlockAds.background =
                ContextCompat.getDrawable(this, R.drawable.bg_stroke_color_red_circle)
            binding.tvTitle.text = HtmlCompat.fromHtml(
                getString(R.string.no_protected_ads),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.clSetupBlock.visibility = if (enableSetupBlock) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun createBlockAds(): ArrayList<BlockAdsData> {
        val list: ArrayList<BlockAdsData> = ArrayList()
        list.add(BlockAdsData("https://www.facebook.com/", 12, true))
        list.add(BlockAdsData("https://www.facebook.com/", 15, false))
        list.add(BlockAdsData("https://www.facebook.com/", 7, true))
        list.add(BlockAdsData("https://www.facebook.com/", 19, false))
        list.add(BlockAdsData("https://www.facebook.com/", 54, false))
        return list
    }

    private fun initSetupBlock() {
        binding.itemBlockAdsApp.setData(
            arrayListOf(
                Subject(
                    "Instagram",
                    "instagram",
                    R.drawable.ic_instagram
                ),
                Subject(
                    "Youtube",
                    "youtube",
                    R.drawable.ic_youtube
                ),
                Subject(
                    "Spotify",
                    "spotify",
                    R.drawable.ic_spotify
                ),
                Subject(
                    "Facebook",
                    "facebook",
                    R.drawable.ic_facebook
                )
            )
        )
        binding.itemBlockAdsApp.setExpanded(false)
        binding.itemBlockAdsApp.disableExpanded()
    }

}