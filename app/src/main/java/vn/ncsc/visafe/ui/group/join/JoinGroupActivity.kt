package vn.ncsc.visafe.ui.group.join

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_join_group.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityJoinGroupBinding
import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.UsersGroupInfo
import vn.ncsc.visafe.model.request.UserInGroupRequest
import vn.ncsc.visafe.model.response.AddMemberInGroupResponse
import vn.ncsc.visafe.ui.create.group.SuccessDialogFragment
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.group.detail.member.MemberManagementActivity
import vn.ncsc.visafe.utils.formatMobileHead84
import vn.ncsc.visafe.utils.isNumber
import vn.ncsc.visafe.utils.setOnSingClickListener

class JoinGroupActivity : BaseActivity() {
    companion object {
        const val SCAN_CODE = "SCAN_CODE"
        const val NEW_MEMBER = "NEW_MEMBER"
    }

    private lateinit var binding: ActivityJoinGroupBinding
    private var groupData: GroupData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            groupData = it.getParcelableExtra(MemberManagementActivity.KEY_DATA)
        }
        binding = ActivityJoinGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener {
            finish()
        }
        initView()
    }

    private fun initView() {
        binding.tvNameGroup.text = groupData?.name
        enableButton()
        binding.edtInputEmail.addTextChangedListener {
            enableButton()
        }
        binding.tvComplete.setOnSingClickListener {
            addMemberToGroup()
        }
    }

    private fun addMemberToGroup() {
        val userNames: Array<String> = if (isNumber(binding.edtInputEmail.text.toString())) {
            arrayOf(formatMobileHead84(binding.edtInputEmail.text.toString()).toString())
        } else {
            arrayOf(binding.edtInputEmail.text.toString())
        }
        val userInGroupRequest = UserInGroupRequest(groupId = groupData?.groupid, usernames = userNames)
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doInviteUserIntoGroup(userInGroupRequest)
        call.enqueue(BaseCallback(this, object : Callback<AddMemberInGroupResponse> {
            override fun onResponse(
                call: Call<AddMemberInGroupResponse>,
                response: Response<AddMemberInGroupResponse>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    if (response.body()?.invited?.isNotEmpty() == true) {
                        response.body()?.invited?.let { showDialogComplete(it[0]) }
                    }
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<AddMemberInGroupResponse>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun showDialogComplete(invited: UsersGroupInfo?) {
        val dialog = SuccessDialogFragment.newInstance(
            getString(R.string.join_group_success),
            getString(R.string.content_join_group_success, binding.tvNameGroup.text.toString().trim())
        )
        dialog.show(supportFragmentManager, "")
        dialog.setOnClickListener {
            when (it) {
                Action.CONFIRM -> {
                    val intent = Intent()
                    intent.putExtra(NEW_MEMBER, invited)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun enableButton() {
        val name = binding.edtInputEmail.text.toString()
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