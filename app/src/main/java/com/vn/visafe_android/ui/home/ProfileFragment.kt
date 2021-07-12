package com.vn.visafe_android.ui.home

import android.content.Intent
import android.util.Log
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.base.BaseFragment
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.FragmentProfileBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.model.request.DeleteWorkSpaceRequest
import com.vn.visafe_android.model.request.UpdateNameWorkspaceRequest
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.ui.create.workspace.CreateWorkspaceActivity
import com.vn.visafe_android.ui.dialog.AccountTypeDialogBottomSheet
import com.vn.visafe_android.ui.dialog.OnClickItemAccountType
import com.vn.visafe_android.ui.setting.SettingActivity
import com.vn.visafe_android.ui.support.SupportCenterActivity
import com.vn.visafe_android.utils.SharePreferenceKeyHelper
import com.vn.visafe_android.utils.setOnSingClickListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun layoutRes(): Int = R.layout.fragment_profile
    private var positionTypeChoose = 0
    private var bottomSheet: AccountTypeDialogBottomSheet? = null
    private var listWorkspaceGroupData: MutableList<WorkspaceGroupData> = mutableListOf()

    override fun initView() {
        if ((activity as MainActivity).isLogin()) {
            (activity as MainActivity).user.observe(this, {
                if (it != null) {
                    binding.tvName.text = it.fullName
                    binding.tvPhone.text = it.phoneNumber
                }
            })
            (activity as MainActivity).listWorkSpace.observe(this, {
                if (it != null) {
                    listWorkspaceGroupData.clear()
                    listWorkspaceGroupData.addAll(it)
                    if (listWorkspaceGroupData.size > 0) {
                        binding.tvTypeAccount.text = listWorkspaceGroupData[0].name
                    }
                }
            })
        }
        binding.clType.setOnClickListener {
            if ((activity as BaseActivity).needLogin())
                return@setOnClickListener
            bottomSheet = AccountTypeDialogBottomSheet.newInstance(listWorkspaceGroupData, positionTypeChoose)
            bottomSheet?.show(parentFragmentManager, null)
            bottomSheet?.onClickItemAccountType = object : OnClickItemAccountType {
                override fun onChoosse(data: WorkspaceGroupData, position: Int) {
                    positionTypeChoose = position
                }

                override fun doDeleteWorkSpace(data: WorkspaceGroupData, position: Int) {
                    deleteWorkSpace(data, position)
                }

                override fun doUpdateNameWorkSpace(
                    data: WorkspaceGroupData,
                    name: String,
                    position: Int
                ) {
                    updateNameWorkSpace(data, name, position)
                }

                override fun add() {
                    startActivity(Intent(context, CreateWorkspaceActivity::class.java))
                }

            }
        }
        binding.clSetting.setOnSingClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
        binding.clSupport.setOnSingClickListener {
            startActivity(Intent(context, SupportCenterActivity::class.java))
        }
        binding.ctrlInfo.setOnSingClickListener {
            if ((activity as BaseActivity).needLogin())
                return@setOnSingClickListener
        }
    }

    fun deleteWorkSpace(data: WorkspaceGroupData, position: Int) {
        showProgressDialog()
        val client = NetworkClient()
        val call = context?.let {
            client.client(context = it)
                .doDeleteWorkspace(DeleteWorkSpaceRequest(data.id))
        }
        call?.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    bottomSheet?.deleteWorkSpace(data, position)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun updateWorkSpace(data: WorkspaceGroupData, position: Int) {
        showProgressDialog()
        val updateWorkspaceRequest = WorkspaceGroupData(
            id = data.id,
            name = data.name,
            type = data.type,
            isActive = data.isActive,
            userOwner = data.userOwner,
            isOwner = data.isOwner,
            phishingEnabled = data.phishingEnabled,
            malwareEnabled = data.malwareEnabled,
            logEnabled = data.logEnabled,
            groupIds = data.groupIds,
            members = data.members,
            createdAt = data.createdAt
        )
        val client = NetworkClient()
        val call =
            context?.let { client.client(context = it).doUpdateWorkspace(updateWorkspaceRequest) }
        call?.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    fun updateNameWorkSpace(data: WorkspaceGroupData, newName: String, position: Int) {
        showProgressDialog()
        val updateNameWorkspaceRequest = UpdateNameWorkspaceRequest(data.id, newName)
        val client = NetworkClient()
        val call = context?.let {
            client.client(context = it)
                .doUpdateNameWorkSpace(updateNameWorkspaceRequest)
        }
        call?.enqueue(BaseCallback(this, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    bottomSheet?.updateNameWorkSpace(newName, position)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }
}