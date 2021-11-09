package vn.ncsc.visafe.ui

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.fragment_splash.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityMemberVipBinding
import vn.ncsc.visafe.model.request.ActiveVipRequest
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper

class VipMemberActivity : BaseActivity() {

    private lateinit var binding: ActivityMemberVipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberVipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    var filter = InputFilter { source, start, end, dest, dstart, dend ->
        for (i in start until end) {
            if (Character.isWhitespace(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }

    private fun initView() {

        binding.edtInputKey.filters = arrayOf(filter, InputFilter.LengthFilter(100))

        binding.ivBack.setOnClickListener {
            finish()
        }
        enableButton()

        binding.edtInputKey.setOnFocusChangeListener { v, hasFocus ->
            binding.btnClearTextInputEmail.visibility =
                if (hasFocus && !binding.edtInputKey.text.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnClearTextInputEmail.setOnClickListener { binding.edtInputKey.setText("") }

        binding.edtInputKey.addTextChangedListener {
            if (binding.edtInputKey.text.isNotBlank()) {
                binding.edtInputKey.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_edittext)
                binding.tvWarning.visibility = View.GONE
            } else {
                binding.edtInputKey.background = ContextCompat.getDrawable(applicationContext, R.drawable.bg_edittext_warning)
                binding.tvWarning.visibility = View.VISIBLE
            }
            binding.btnClearTextInputEmail.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            enableButton()
        }
        binding.tvConfirm.setOnClickListener {
            activeKey(binding.edtInputKey.text.toString())
            hideKeyboard(this@VipMemberActivity)
        }
    }

    private fun enableButton() {
        val groupName = binding.edtInputKey.text.toString()
        if (groupName.isNotBlank()) {
            with(binding.tvConfirm) {
                backgroundTintList =
                    resources.getColorStateList(R.color.color_FFB31F, theme)

                setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                isEnabled = true
            }
        } else {
            with(binding.tvConfirm) {
                backgroundTintList =
                    resources.getColorStateList(
                        R.color.color_F8F8F8,
                        theme
                    )
                setTextColor(
                    ContextCompat.getColor(
                        applicationContext, R.color.color_AAAAAA
                    )
                )
                isEnabled = false
            }
        }
    }

    private fun activeKey(key: String) {
        showProgressDialog()
        val deviceId = SharePreferenceKeyHelper.getInstance(application).getString(PreferenceKey.DEVICE_ID)
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doActiveVip(ActiveVipRequest(key = key, deviceId = deviceId))
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val dialog =
                        SuccessDialogFragment.newInstance(title = "Kích hoạt mã thành công", content = "Bạn đã là thành viên VIP")
                    dialog.show(supportFragmentManager, "")
                    dialog.setOnClickListener {
                        when (it) {
                            Action.CONFIRM -> {
                                setResult(RESULT_OK)
                                finish()
                            }
                            else -> {
                                return@setOnClickListener
                            }
                        }
                    }
                } else if (response.code() == NetworkClient.CODE_EXISTS_ACCOUNT) {
                    showToast("Mã xác nhận không đúng, vui lòng thử lại!")
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}