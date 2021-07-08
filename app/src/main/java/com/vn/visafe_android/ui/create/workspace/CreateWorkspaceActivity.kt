package com.vn.visafe_android.ui.create.workspace

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityCreateWorkspaceBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.model.request.CreateWorkSpaceRequest
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.create.workspace.dialog.DialogCreateSuccessWorkSpace
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateWorkspaceActivity : BaseActivity() {
    lateinit var binding: ActivityCreateWorkspaceBinding
    private var totalStep = 2
    private var step = 0
    var createWorkSpaceRequest = CreateWorkSpaceRequest()
    private var isFirst = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateWorkspaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            isFirst = it.getBooleanExtra(MainActivity.IS_FIRST_CREATE_WORKSPACE, false)
        }
        initView()
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        addFragment(ProtectWorkspaceFragment.newInstance())
    }

    fun addFragment(fragment: Fragment, tag: String = "") {
        step++
        setProgressView()
        supportFragmentManager.beginTransaction()
            .add(R.id.frameContainer, fragment)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun doCreateGroup() {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doCreateWorkspace(createWorkSpaceRequest)
        call.enqueue(BaseCallback(this@CreateWorkspaceActivity, object : Callback<WorkspaceGroupData> {
            override fun onResponse(
                call: Call<WorkspaceGroupData>,
                response: Response<WorkspaceGroupData>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_CREATED) {
                    showDialog()
                }
            }

            override fun onFailure(call: Call<WorkspaceGroupData>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun showDialog() {
        val bottomSheet = DialogCreateSuccessWorkSpace()
        bottomSheet.isCancelable = false
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener {
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

    private fun setProgressView() {
        binding.progress.setProgress(step * (100 / totalStep))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        step--
        setProgressView()
        if (step == 0) {
            finish()
        }
    }
}