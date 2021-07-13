package vn.ncsc.visafe.ui.protect

import android.os.Bundle
import android.view.View
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityBlockAdsBinding
import vn.ncsc.visafe.utils.OnSingleClickListener

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