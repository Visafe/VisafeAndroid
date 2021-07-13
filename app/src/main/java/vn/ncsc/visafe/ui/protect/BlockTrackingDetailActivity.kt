package vn.ncsc.visafe.ui.protect

import android.os.Bundle
import android.view.View
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityBlockTrackingDetailBinding
import vn.ncsc.visafe.utils.OnSingleClickListener

class BlockTrackingDetailActivity : BaseActivity() {
    companion object {
        const val BLOCK_TRACKING_KEY = "BLOCK_TRACKING_KEY"
    }

    lateinit var binding: ActivityBlockTrackingDetailBinding

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
    }
}