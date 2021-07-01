package com.vn.visafe_android.ui.create.group

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateGroupActivity : BaseActivity() {
    lateinit var binding: ActivityCreateGroupBinding

    private var totalStep = 5

    private var step = 0
    var createGroupRequest = CreateGroupRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        supportFragmentManager.addOnBackStackChangedListener {
            val fragments = supportFragmentManager.fragments
            if (fragments.isNullOrEmpty()) {
                return@addOnBackStackChangedListener
            }
            if (fragments[fragments.size - 1] is ProtectedGroupFragment) {
                binding.tvReset.visibility = View.GONE
            } else {
                binding.tvReset.visibility = View.VISIBLE
            }
        }

        addFragment(ProtectedGroupFragment.newInstance())


        binding.ivBack.setOnClickListener {
//            supportFragmentManager.popBackStack()
//            step--
//            setProgressView()
            finish()
        }

        binding.tvReset.setOnClickListener {
            // TODO: 6/30/2021  reset
        }
    }

    fun addFragment(fragment: Fragment, tag: String = "") {
        step++
        setProgressView()
        supportFragmentManager.beginTransaction()
            .add(R.id.frameContainer, fragment)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun showReset() {
        binding.tvReset.visibility = View.VISIBLE
    }

    private fun setProgressView() {
        binding.progress.setProgress(step * (100 / totalStep))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        step--
        setProgressView()

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