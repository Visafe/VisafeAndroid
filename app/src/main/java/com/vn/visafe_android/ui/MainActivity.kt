package com.vn.visafe_android.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.vn.visafe_android.R
import com.vn.visafe_android.ViSafeApp
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.data.BaseCallback
import com.vn.visafe_android.data.NetworkClient
import com.vn.visafe_android.databinding.ActivityMainBinding
import com.vn.visafe_android.model.UserInfo
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.model.request.DeleteWorkSpaceRequest
import com.vn.visafe_android.model.request.UpdateNameWorkspaceRequest
import com.vn.visafe_android.ui.authentication.splash.PlaceholderFragment
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.dialog.VisafeDialogBottomSheet
import com.vn.visafe_android.ui.home.*
import com.vn.visafe_android.utils.PreferenceKey
import com.vn.visafe_android.utils.SharePreferenceKeyHelper
import com.vn.visafe_android.utils.setBackgroundTint
import kotlinx.android.synthetic.main.fragment_splash.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val POSITION_PROTECT = 0
        const val POSITION_GROUP = 1
        const val POSITION_SCAN = 2
        const val POSITION_NOTIFICATION = 3
        const val POSITION_PROFILE = 4
        const val IS_FIRST_CREATE_WORKSPACE = "IS_FIRST_CREATE_WORKSPACE"
    }

    lateinit var binding: ActivityMainBinding

    private var listFragment = mutableListOf<Fragment>()
    private var listMenu: MutableList<WorkspaceGroupData> = mutableListOf()
    private var currentPosition = 0
    private var overViewProtectFragment = OverViewProtectFragment()
    private var groupManagementFragment = GroupManagementFragment()
    private var profileFragment = ProfileFragment()

    var user: MutableLiveData<UserInfo> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        doGetWorkSpaces()
        doGetUserInfo()
    }

    private fun initView() {
        initTab()
    }

    private fun initTab() {
        listFragment.add(POSITION_PROTECT, overViewProtectFragment)
        listFragment.add(POSITION_GROUP, groupManagementFragment)
        listFragment.add(POSITION_SCAN, HomeFragment())
        listFragment.add(POSITION_NOTIFICATION, NotificationFragment())
        listFragment.add(POSITION_PROFILE, profileFragment)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        listFragment.forEachIndexed { index, fragment ->
            fragmentTransaction.add(R.id.fr_container, fragment, fragment.javaClass.simpleName)
            if (index == POSITION_SCAN) {
                fragmentTransaction.show(fragment)
            } else {
                fragmentTransaction.hide(fragment)
            }
        }
        fragmentTransaction.commitAllowingStateLoss()
        openTab(POSITION_SCAN)
        binding.bottomView.setOnNavigationItemSelectedListener(this)
    }

    private fun showDialogDeleteWorkSpace(data: WorkspaceGroupData, position: Int) {
        val bottomSheet = VisafeDialogBottomSheet.newInstance(
            "",
            getString(R.string.delete_workspace_content, data.name),
            VisafeDialogBottomSheet.TYPE_CONFIRM
        )
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener { inputText, action ->
            when (action) {
                Action.CONFIRM -> {
                    doDeleteWorkSpace(data, position)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun showDialogUpdateNameWorkSpace(data: WorkspaceGroupData, position: Int) {
        val bottomSheet = data.name?.let {
            VisafeDialogBottomSheet.newInstanceEdit(
                "",
                getString(R.string.update_name_workspace),
                VisafeDialogBottomSheet.TYPE_SAVE,
                "", it
            )
        }
        bottomSheet?.show(supportFragmentManager, null)
        bottomSheet?.setOnClickListener { inputText, action ->
            when (action) {
                Action.SAVE -> {
                    doUpdateNameWorkSpace(data, inputText, position)
                }
                else -> {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun doGetWorkSpaces() {
        if (!SharePreferenceKeyHelper.getInstance(application).isLogin())
            return
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doGetWorkSpacesOfCurrentUser()
        call.enqueue(BaseCallback(this@MainActivity, object : Callback<List<WorkspaceGroupData>> {
            override fun onResponse(
                call: Call<List<WorkspaceGroupData>>,
                response: Response<List<WorkspaceGroupData>>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    listMenu.clear()
                    response.body()?.toMutableList()?.let { listMenu.addAll(it) }
                    if (listMenu.size > 0) {
                        for (i in listMenu.indices) {
                            listMenu[i].isSelected = i == 0
                        }
                        listMenu[0].let { workspaceGroupData ->
                            if (groupManagementFragment != null) {
                                groupManagementFragment.updateWorkspace(workspaceGroupData)
                            }
                        }

                    }
                }
                dismissProgress()
            }

            override fun onFailure(call: Call<List<WorkspaceGroupData>>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun doDeleteWorkSpace(data: WorkspaceGroupData, position: Int) {
        showProgressDialog()
        val client = NetworkClient()
        val call = client.client(context = applicationContext)
            .doDeleteWorkspace(DeleteWorkSpaceRequest(data.id))
        call.enqueue(BaseCallback(this@MainActivity, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun doUpdateWorkSpace(data: WorkspaceGroupData, position: Int) {
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
            client.client(context = applicationContext).doUpdateWorkspace(updateWorkspaceRequest)
        call.enqueue(BaseCallback(this@MainActivity, object : Callback<ResponseBody> {
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

    private fun doUpdateNameWorkSpace(data: WorkspaceGroupData, newName: String, position: Int) {
        showProgressDialog()
        val updateNameWorkspaceRequest = UpdateNameWorkspaceRequest(data.id, newName)
        val client = NetworkClient()
        val call = client.client(context = applicationContext)
            .doUpdateNameWorkSpace(updateNameWorkspaceRequest)
        call.enqueue(BaseCallback(this@MainActivity, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun doGetUserInfo() {
        if (!SharePreferenceKeyHelper.getInstance(application).isLogin())
            return
        if (ViSafeApp().getPreference().getUserInfo().userID == null) {
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetUserInfo()
            call.enqueue(BaseCallback(this@MainActivity, object : Callback<UserInfo> {
                override fun onResponse(
                    call: Call<UserInfo>,
                    response: Response<UserInfo>
                ) {
                    dismissProgress()
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        val gson = Gson()
                        val userInfo = response.body()
                        userInfo?.let {
                            user.value = it
                        }
                        ViSafeApp().getPreference().putString(
                            PreferenceKey.USER_INFO,
                            gson.toJson(userInfo)
                        )
                    }
                }

                override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                    dismissProgress()
                }
            }))
        } else {
            dismissProgress()
            val userInfo = ViSafeApp().getPreference().getUserInfo()
            userInfo.let {
                user.value = it
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (needLogin()) {
            return false
        }
        when (item.itemId) {
            R.id.navigation_protect -> {
                openTab(POSITION_PROTECT)
                return true
            }
            R.id.navigation_group -> {
                openTab(POSITION_GROUP)
                return true
            }
            R.id.navigation_scan -> {
                openTab(POSITION_SCAN)
                return true
            }
            R.id.navigation_notification -> {
                openTab(POSITION_NOTIFICATION)
                return true
            }
            R.id.navigation_profile -> {
                openTab(POSITION_PROFILE)
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun openTab(position: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        listFragment.forEachIndexed { index, fragment ->
            if (index == position) {
                fragmentTransaction.show(fragment)
            } else {
                fragmentTransaction.hide(fragment)
            }
        }
        if (position > currentPosition) {
            fragmentTransaction.setCustomAnimations(
                R.anim.trans_right_in,
                R.anim.trans_right_in
            )
        } else {
            fragmentTransaction.setCustomAnimations(
                R.anim.trans_right_out,
                R.anim.trans_right_out
            )
        }
        currentPosition = position
        fragmentTransaction.commitNowAllowingStateLoss()
        changeColorTab(position)
    }

    @SuppressLint("ResourceType")
    private fun changeColorTab(position: Int) {
        when (position) {
            POSITION_SCAN -> {
                binding.bottomView.setBackgroundResource(R.color.color_0B1847)
                binding.bottomView.itemIconTintList =
                    AppCompatResources.getColorStateList(this, R.color.white_60)
                binding.bottomView.itemTextColor =
                    AppCompatResources.getColorStateList(this, R.color.white_60)
                binding.fab.setBackgroundTint(R.color.colorPrimary)
            }
            else -> {
                binding.bottomView.setBackgroundResource(R.color.white)
                binding.bottomView.itemIconTintList =
                    AppCompatResources.getColorStateList(this, R.drawable.tab_color)
                binding.bottomView.itemTextColor =
                    AppCompatResources.getColorStateList(this, R.drawable.tab_color)
                binding.fab.setBackgroundTint(R.color.color_061448)
            }
        }
    }

}