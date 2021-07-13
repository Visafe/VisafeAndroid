package com.vn.visafe_android.ui.protect

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityBlockAdsBinding
import com.vn.visafe_android.utils.OnSingleClickListener

class BlockAdsActivity : BaseActivity() {
    companion object {
        const val BLOCK_ADS_KEY = "BLOCK_ADS_KEY"
    }

    lateinit var binding: ActivityBlockAdsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockAdsBinding.inflate(layoutInflater)
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