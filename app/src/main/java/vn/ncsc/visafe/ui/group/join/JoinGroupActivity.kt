package vn.ncsc.visafe.ui.group.join

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.BaseResponse
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityJoinGroupBinding
import vn.ncsc.visafe.model.request.AddDeviceRequest
import vn.ncsc.visafe.model.response.BotnetResponse
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.utils.setOnSingClickListener

class JoinGroupActivity : BaseActivity() {
    companion object {
        const val GROUP_ID = "GROUP_ID"
        const val GROUP_NAME = "GROUP_NAME"
    }

    private lateinit var binding: ActivityJoinGroupBinding
    private var groupId: String? = ""
    private var groupName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            groupId = it.getStringExtra(GROUP_ID)
            groupName = it.getStringExtra(GROUP_NAME)
        }
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
            addDeviceToGroup(groupId, groupName, binding.etName.text.toString())
        }
    }

    private fun showDialogComplete() {
        val dialog = SuccessDialogFragment.newInstance(
            getString(R.string.join_group_success),
            getString(R.string.content_join_group_success, binding.tvNameGroup.text.toString().trim())
        )
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

    private fun addDeviceToGroup(groupId: String?, groupName: String?, deviceOwner: String) {
        val macAddress = getMacAddress()
        val ipAddress = getIpAddress()
        val deviceName = Build.MANUFACTURER.plus(" ").plus(Build.MODEL)
        val deviceId = SharePreferenceKeyHelper.getInstance(application).getString(PreferenceKey.DEVICE_ID)
        val addDeviceRequest = AddDeviceRequest(
            deviceId = deviceId,
            groupName = groupName,
            groupId = groupId,
            deviceName = deviceName,
            macAddress = macAddress,
            ipAddress = ipAddress,
            deviceType = "Mobile",
            deviceOwner = deviceOwner,
            deviceDetail = "ABC"
        )
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doAddDeviceToGroup(addDeviceRequest)
        call.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    showDialogComplete()
                } else if (response.code() == NetworkClient.CODE_EXISTS_ACCOUNT) {
                    response.errorBody()?.let {
                        val buffer = response.errorBody()?.source()?.buffer?.readByteArray()
                        val dataString = buffer?.decodeToString()
                        val responseData = Gson().fromJson(dataString, BaseResponse::class.java)
                        responseData.msg?.let {
                            val builder = AlertDialog.Builder(this@JoinGroupActivity)
                            with(builder)
                            {
                                setTitle(getString(R.string.thong_bao))
                                setMessage(it)
                                setPositiveButton(
                                    getString(R.string.dong_y)
                                ) { _, _ -> finish() }
                                show()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}