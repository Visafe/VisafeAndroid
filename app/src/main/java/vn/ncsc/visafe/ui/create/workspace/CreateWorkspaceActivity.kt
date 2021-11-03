package vn.ncsc.visafe.ui.create.workspace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityCreateWorkspaceBinding
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.model.request.CreateWorkSpaceRequest
import vn.ncsc.visafe.ui.MainActivity
import vn.ncsc.visafe.ui.create.group.access_manager.Action
import vn.ncsc.visafe.ui.create.workspace.dialog.DialogCreateSuccessWorkSpace

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
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    response.body()?.let { showDialog(it) }
                }
            }

            override fun onFailure(call: Call<WorkspaceGroupData>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun showDialog(workspaceGroupData: WorkspaceGroupData) {
        val bottomSheet = DialogCreateSuccessWorkSpace()
        bottomSheet.isCancelable = false
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener {
            when (it) {
                Action.CONFIRM -> {
                    val intentAddNewWorkspace = Intent()
                    intentAddNewWorkspace.putExtra("NewWorkSpace", workspaceGroupData)
                    setResult(RESULT_OK, intentAddNewWorkspace)
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