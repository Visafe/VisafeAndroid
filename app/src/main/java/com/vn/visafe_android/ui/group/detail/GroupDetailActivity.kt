package com.vn.visafe_android.ui.group.detail

import android.os.Bundle
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityGroupDetailBinding

class GroupDetailActivity : BaseActivity() {
    lateinit var binding: ActivityGroupDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

    }
}