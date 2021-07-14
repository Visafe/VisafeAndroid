package vn.ncsc.visafe.ui.group.detail.member

import android.os.Bundle
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.databinding.ActivityAddMemberBinding
import vn.ncsc.visafe.utils.setOnSingClickListener

class AddMemberActivity : BaseActivity() {
    lateinit var binding: ActivityAddMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initControl()
    }

    private fun initView() {

    }

    private fun initControl() {
        binding.ivBack.setOnSingClickListener { finish() }
    }
}