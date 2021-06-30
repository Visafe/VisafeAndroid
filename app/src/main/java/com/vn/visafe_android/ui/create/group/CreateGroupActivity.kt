package com.vn.visafe_android.ui.create.group

import android.os.Bundle
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityCreateGroupBinding

class CreateGroupActivity : BaseActivity() {
    lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

    }
}