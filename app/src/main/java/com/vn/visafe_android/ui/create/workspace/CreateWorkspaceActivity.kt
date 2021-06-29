package com.vn.visafe_android.ui.create.workspace

import android.os.Bundle
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityCreateWorkspaceBinding

class CreateWorkspaceActivity : BaseActivity() {
    lateinit var binding: ActivityCreateWorkspaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateWorkspaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

    }
}