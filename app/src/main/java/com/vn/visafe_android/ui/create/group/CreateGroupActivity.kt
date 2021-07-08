package com.vn.visafe_android.ui.create.group

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityCreateGroupBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.model.request.CreateGroupRequest
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.create.group.protected_group.ProtectedGroupFragment
import com.vn.visafe_android.ui.home.administrator.GroupManagementFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateGroupActivity : BaseActivity() {
    lateinit var binding: ActivityCreateGroupBinding
    private var step = 0
    var createGroupRequest = CreateGroupRequest()
    private var workspaceGroupData: WorkspaceGroupData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            workspaceGroupData = it.getParcelableExtra(GroupManagementFragment.DATA_WORKSPACE)
        }
        workspaceGroupData?.let {
            createGroupRequest.workspace_id = it.id
            createGroupRequest.malware_enabled = it.malwareEnabled
            createGroupRequest.phishing_enabled = it.phishingEnabled
        }
        initView()
    }

    private fun initView() {
        addFragment(WelcomeCreateGroupFragment())
    }

    fun addFragment(fragment: Fragment, tag: String = "") {
        step++
        supportFragmentManager.beginTransaction()
            .add(R.id.frameContainer, fragment)
            .setCustomAnimations(
                R.anim.slide_in_left_1, R.anim.slide_out_left_1,
                R.anim.slide_out_right_1, R.anim.slide_in_right_1
            )
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        step--
        if (step == 1) {
            finish()
        }
    }

    fun doCreateGroup() {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doCreateGroup(createGroupRequest)
        call.enqueue(BaseCallback(this@CreateGroupActivity, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val dialog = SuccessDialogFragment.newInstance()
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
        }))
    }
}