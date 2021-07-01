package com.vn.visafe_android.ui.create.workspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityCreateWorkspaceBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.create.group.CreateGroupActivity

class CreateWorkspaceActivity : BaseActivity() {
    lateinit var binding: ActivityCreateWorkspaceBinding
    private var totalStep = 2
    private var step = 0
    var groupData = WorkspaceGroupData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateWorkspaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        addFragment(ProtectWorkspaceFragment.newInstance())
    }

    fun addFragment(fragment: Fragment, tag: String = "") {
        step++
        setProgressView()
        supportFragmentManager.beginTransaction()
            .add(R.id.frameContainer, fragment)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    private fun setProgressView() {
        binding.progress.setProgress(step * (100 / totalStep))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        step--
        setProgressView()
        if (step == 0) {
            finish()
        }
    }
}