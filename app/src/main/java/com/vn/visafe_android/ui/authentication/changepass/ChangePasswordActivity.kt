package com.vn.visafe_android.ui.authentication.changepass

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityChangePasswordBinding
import com.vn.visafe_android.model.request.ChangePasswordRequest
import com.vn.visafe_android.ui.create.group.SuccessDialogFragment
import com.vn.visafe_android.ui.create.group.access_manager.Action
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ChangePasswordActivity : BaseActivity() {

    lateinit var binding: ActivityChangePasswordBinding

    var changePasswordRequest = ChangePasswordRequest()

    companion object {
        var rootId: Int = R.id.fragment_pass
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        handlerFragment(CurrentPassFragment(), rootId, tag = "CurrentPassFragment")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val manager: FragmentManager = supportFragmentManager
        if (manager.fragments.isEmpty()) {
            finish()
        }
    }

    fun doChangePassword() {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.clientWithoutToken(context = applicationContext).doChangePassword(changePasswordRequest)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val dialog = SuccessDialogFragment.newInstance("Đổi password thành công", "")
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
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        })
    }
}