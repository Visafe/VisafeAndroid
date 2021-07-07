package com.vn.visafe_android.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.vn.visafe_android.model.request.UpdateWorkspaceRequest
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.create.workspace.CreateWorkspaceActivity
import com.vn.visafe_android.ui.dialog.VisafeDialogBottomSheet
import com.vn.visafe_android.ui.home.*
import com.vn.visafe_android.ui.home.administrator.AdministratorFragment
import com.vn.visafe_android.utils.PreferenceKey
import com.vn.visafe_android.utils.setOnSingClickListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {
    companion object {
        const val POSITION_HOME = 0
        const val POSITION_PROTECT = 1
        const val POSITION_UTILITIES = 2
        const val POSITION_SETTING = 3
        const val REQUEST_CODE_CREATE_WORKSPACE = 123
        const val IS_FIRST_CREATE_WORKSPACE = "IS_FIRST_CREATE_WORKSPACE"
    }

    lateinit var binding: ActivityMainBinding

    private var listFragment = mutableListOf<Fragment>()
    private var currentPosition = 0
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var adapter: MenuAdapter? = null
    private var workspaceGroupData: WorkspaceGroupData? = null
    private var listMenu: MutableList<WorkspaceGroupData> = mutableListOf()
    private var administratorFragment = AdministratorFragment()
    private var protectFragment = ProtectFragment()
    private var utilitiesHomeFragment = UtilitiesHomeFragment()
    private var settingFragment = SettingFragment()

    var resultLauncherCreateWorkspaceActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            doGetWorkSpaces()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        doGetWorkSpaces()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.dlMain,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.dlMain.addDrawerListener(drawerToggle!!)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.title = ""
        initListMenu()
        initTab()
        binding.tvAddWorkspace.setOnSingClickListener {
            resultLauncherCreateWorkspaceActivity.launch(Intent(this, CreateWorkspaceActivity::class.java))
        }
    }

    private fun initTab() {
        listFragment.add(POSITION_HOME, administratorFragment)
        listFragment.add(POSITION_PROTECT, protectFragment)
        listFragment.add(POSITION_UTILITIES, utilitiesHomeFragment)
        listFragment.add(POSITION_SETTING, settingFragment)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        listFragment.forEachIndexed { index, fragment ->
            fragmentTransaction.add(R.id.fr_container, fragment, fragment.javaClass.simpleName)
            if (index != 0) {
                fragmentTransaction.hide(fragment)
            } else {
                fragmentTransaction.show(fragment)
            }
        }
        fragmentTransaction.commitAllowingStateLoss()

        binding.mainContent.bottomView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    openTab(POSITION_HOME)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_protect -> {
                    openTab(POSITION_PROTECT)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_utilities -> {
                    openTab(POSITION_UTILITIES)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_setting -> {
                    openTab(POSITION_SETTING)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun initListMenu() {
        binding.rvGroup.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        listMenu = createListMenu()
        adapter = MenuAdapter(listMenu, object : OnClickMenu {
            override fun onClickMenu(data: WorkspaceGroupData, position: Int) {
                adapter?.setSelected(position)
                binding.tvTitleToolbar.text = listMenu[position].name
                data.let {
                    administratorFragment.updateDataView(it)
                }
            }

            override fun onMoreGroup(data: WorkspaceGroupData, position: Int) {
                val bottomSheet = VisafeDialogBottomSheet.newInstance(
                    getString(R.string.workspaces),
                    data.name!!,
                    VisafeDialogBottomSheet.TYPE_EDIT,
                    getString(R.string.edit_workspace),
                    getString(R.string.delete_workspace)
                )
                bottomSheet.show(supportFragmentManager, null)
                bottomSheet.setOnClickListener { inputText, action ->
                    when (action) {
                        Action.DELETE -> {
                            showDialogDeleteWorkSpace(data, position)
                        }
                        Action.EDIT -> {
                            showDialogUpdateNameWorkSpace(data, position)
                        }
                        else -> {
                            return@setOnClickListener
                        }
                    }
                }
            }
        })
        binding.rvGroup.adapter = adapter
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

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    private fun openTab(position: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        listFragment.forEachIndexed { index, fragment ->
            if (index == position) {
//                fragmentTransaction.setCustomAnimations(R.anim.trans_right_in, R.anim.trans_right_in)
                fragmentTransaction.show(fragment)
            } else {
//                fragmentTransaction.setCustomAnimations(R.anim.trans_right_out, R.anim.trans_right_out)
                fragmentTransaction.hide(fragment)
            }
        }
        if (position > currentPosition) {
            fragmentTransaction.setCustomAnimations(R.anim.trans_right_in, R.anim.trans_right_in)
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.trans_right_out, R.anim.trans_right_out)
        }
        currentPosition = position
        fragmentTransaction.commitNowAllowingStateLoss()
        changeColorTab(position)
    }

    private fun changeColorTab(position: Int) {
        when (position) {
            POSITION_PROTECT -> {
                binding.mainContent.bottomView.setBackgroundResource(R.color.color_0B1847)
                binding.toolbar.visibility = View.GONE
//                binding.mainContent.bottomView.itemIconTintList =
//                    AppCompatResources.getColorStateList(this, R.color.white)
//                binding.mainContent.bottomView.itemTextColor =
//                    AppCompatResources.getColorStateList(this, R.color.white)
            }
            else -> {
                binding.mainContent.bottomView.setBackgroundResource(R.color.white)
                binding.toolbar.visibility = View.VISIBLE
//                binding.mainContent.bottomView.itemIconTintList =
//                    AppCompatResources.getColorStateList(this, R.color.color_111111)
//                binding.mainContent.bottomView.itemTextColor =
//                    AppCompatResources.getColorStateList(this, R.color.color_111111)
            }
        }
    }

    private fun doGetWorkSpaces() {
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
                        adapter?.notifyDataSetChanged()
                        workspaceGroupData = listMenu[0]
                        binding.tvTitleToolbar.text = listMenu[0].name
                        workspaceGroupData?.let {
                            administratorFragment.updateDataView(it)
                        }
                        doGetUserInfo()
                    } else {
                        val intent = Intent(this@MainActivity, CreateWorkspaceActivity::class.java)
                        intent.putExtra(IS_FIRST_CREATE_WORKSPACE, true)
                        resultLauncherCreateWorkspaceActivity.launch(intent)
                    }
                }
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
        val call = client.client(context = applicationContext).doDeleteWorkspace(DeleteWorkSpaceRequest(data.id))
        call.enqueue(BaseCallback(this@MainActivity, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    adapter?.deleteItem(data, position)
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
        val call = client.client(context = applicationContext).doUpdateWorkspace(updateWorkspaceRequest)
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
        val call = client.client(context = applicationContext).doUpdateNameWorkSpace(updateNameWorkspaceRequest)
        call.enqueue(BaseCallback(this@MainActivity, object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    adapter?.updateName(newName, position)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun doGetUserInfo() {
        if (ViSafeApp().getPreference().getUserInfo().userID == null) {
            showProgressDialog()
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
                            binding.tvUserName.text = it.fullName.toString()
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
            val userInfo = ViSafeApp().getPreference().getUserInfo()
            userInfo.let {
                binding.tvUserName.text = it.fullName.toString()
            }
        }

    }
}