package com.vn.visafe_android.ui.group.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityGroupDetailBinding
import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.ui.group.detail.member.GroupDetailMemberManagementFragment

class GroupDetailActivity : BaseActivity() {
    lateinit var binding: ActivityGroupDetailBinding

    companion object {
        const val TYPE_CONFIGURATION_KEY = "TYPE_CONFIGURATION_KEY"
        const val TYPE_GROUP_DATA_KEY = "TYPE_GROUP_DATA_KEY"

        const val TYPE_CHAN_THEO_DOI_QUANG_CAO = "TYPE_CHAN_THEO_DOI_QUANG_CAO"
        const val TYPE_QUAN_LY_TRUY_CAP = "TYPE_QUAN_LY_TRUY_CAP"
        const val TYPE_KHUNG_GIO_BAO_VE = "TYPE_KHUNG_GIO_BAO_VE"
        const val TYPE_QUAN_LY_THIET_BI = "TYPE_QUAN_LY_THIET_BI"
        const val TYPE_QUAN_LY_THANH_VIEN = "TYPE_QUAN_LY_THANH_VIEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val data = intent.extras?.get(TYPE_GROUP_DATA_KEY) as GroupData
        Toast.makeText(this, data.name, Toast.LENGTH_SHORT).show()
        val type = intent.extras?.getString(TYPE_CONFIGURATION_KEY, "")
        if (type != null && type.isNotEmpty()) {
            when (type) {
                TYPE_CHAN_THEO_DOI_QUANG_CAO -> {
                    binding.progressView.visibility = View.VISIBLE
                    binding.llTitle.visibility = View.GONE
                    binding.tvReset.visibility = View.VISIBLE
                    addFragment(GroupDetailBlockTrackingFragment(), "")
                }
                TYPE_QUAN_LY_TRUY_CAP -> {
                    binding.progressView.visibility = View.VISIBLE
                    binding.llTitle.visibility = View.GONE
                    binding.tvReset.visibility = View.VISIBLE
                    addFragment(GroupDetailAccessManagementFragment(), "")
                }
                TYPE_KHUNG_GIO_BAO_VE -> {
                    binding.progressView.visibility = View.VISIBLE
                    binding.llTitle.visibility = View.GONE
                    binding.tvReset.visibility = View.VISIBLE
                    addFragment(GroupDetailProtectTimeFragment(), "")
                }
                TYPE_QUAN_LY_THIET_BI -> {
                    binding.progressView.visibility = View.GONE
                    binding.llTitle.visibility = View.VISIBLE
                    binding.tvReset.visibility = View.GONE
                    binding.tvTitle.text = getString(R.string.quan_ly_thiet_bi)
                    binding.tvContent.text = data.name
                    addFragment(GroupDetailDeviceManagementFragment(), "")
                }
                TYPE_QUAN_LY_THANH_VIEN -> {
                    binding.llTitle.visibility = View.VISIBLE
                    binding.tvReset.visibility = View.GONE
                    binding.progressView.visibility = View.GONE
                    binding.tvTitle.text = getString(R.string.member_management_title)
                    binding.tvContent.text = data.name
                    addFragment(GroupDetailMemberManagementFragment(), "")
                }
                else -> {
                    return
                }

            }
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    fun addFragment(fragment: Fragment, tag: String = "") {
        supportFragmentManager.beginTransaction()
            .add(R.id.frameContainer, fragment)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        finish()
    }
}