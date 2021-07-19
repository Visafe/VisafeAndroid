package vn.ncsc.visafe.ui.authentication.changepass

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityChangePasswordBinding
import vn.ncsc.visafe.model.request.ChangePasswordRequest

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
        val call = client.client(context = applicationContext).doChangePassword(changePasswordRequest)
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