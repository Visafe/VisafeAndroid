package vn.ncsc.visafe.ui.create.group

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityCreateGroupBinding
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.model.request.CreateGroupRequest
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.home.GroupManagementFragment
import java.util.*

class CreateGroupActivity : BaseActivity() {
    lateinit var binding: ActivityCreateGroupBinding
    private var step = -1
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
        workspaceGroupData?.let {
            addFragment(WelcomeCreateGroupFragment.newInstance(it))
        }
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
        if (step == 0 || step == 1) {
            finish()
        } else {
            step--
            super.onBackPressed()
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
                                setResult(RESULT_OK)
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