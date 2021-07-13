package com.vn.visafe_android.ui.protect

import android.os.Bundle
import android.view.View
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityBlockTrackingDetailBinding
import com.vn.visafe_android.utils.OnSingleClickListener

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