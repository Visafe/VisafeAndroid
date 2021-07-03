package com.vn.visafe_android.ui.group.join

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityJoinGroupBinding
import com.vn.visafe_android.ui.create.group.SuccessDialogFragment
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.utils.setOnSingClickListener

class JoinGroupActivity : BaseActivity() {
    companion object {
        const val SCAN_CODE = "SCAN_CODE"
    }

    private lateinit var binding: ActivityJoinGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener {
            finish()
        }
        initView()
    }

    private fun initView() {
        enableButton()
        binding.etName.addTextChangedListener {
            enableButton()
        }
        binding.tvComplete.setOnSingClickListener {
            showDialogComplete()
        }
    }

    private fun showDialogComplete() {
        val dialog = SuccessDialogFragment.newInstance(
            getString(R.string.join_group_success),
            getString(R.string.content_join_group_success, binding.tvNameGroup.text.toString().trim()))
        dialog.show(supportFragmentManager, "")
        dialog.setOnClickListener {
            when (it) {
                Action.CONFIRM -> {
                    finish()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }


    private fun enableButton() {
        val name = binding.etName.text.toString()
        if (name.isNotBlank()) {
            with(binding.tvComplete) {
                backgroundTintList =
                    resources.getColorStateList(R.color.color_FFB31F, theme)

                setTextColor(ContextCompat.getColor(this@JoinGroupActivity, R.color.white))
                isEnabled = true
            }
        } else {
            with(binding.tvComplete) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_F8F8F8,
                        theme
                    )
                setTextColor(
                    ContextCompat.getColor(
                        this@JoinGroupActivity,
                        R.color.color_111111
                    )
                )
                isEnabled = false
            }
        }
    }
}