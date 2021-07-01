package com.vn.visafe_android.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
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
import com.vn.visafe_android.ui.create.group.access_manager.Action
import com.vn.visafe_android.ui.create.workspace.CreateWorkspaceActivity
import com.vn.visafe_android.ui.create.workspace.dialog.DeleteWorkspaceBottomSheet
import com.vn.visafe_android.ui.create.workspace.dialog.EditWorkspaceBottomSheet
import com.vn.visafe_android.ui.home.*
import com.vn.visafe_android.ui.home.administrator.AdministratorFragment
import com.vn.visafe_android.utils.PreferenceKey
import com.vn.visafe_android.utils.setOnSingClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {
    companion object {
        const val POSITION_HOME = 0
        const val POSITION_PROTECT = 1
        const val POSITION_UTILITIES = 2
        const val POSITION_SETTING = 3
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        doGetUserInfo()
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
            startActivity(Intent(this, CreateWorkspaceActivity::class.java))
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
        adapter = MenuAdapter((listMenu as ArrayList), object : OnClickMenu {
            override fun onClickMenu(data: WorkspaceGroupData, position: Int) {
                adapter?.setSelected(position)
                binding.tvTitleToolbar.text = listMenu[position].name
            }

            override fun onMoreGroup(data: WorkspaceGroupData, position: Int) {
                val bottomSheet = EditWorkspaceBottomSheet.newInstance(data)
                bottomSheet.show(supportFragmentManager, null)
                bottomSheet.setOnClickListener {
                    when (it) {
                        Action.DELETE -> {
                            showDialogDeleteWorkSpace(data, position)
                        }

                        Action.EDIT -> {
                            //Edit workspace
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
        val bottomSheet = DeleteWorkspaceBottomSheet.newInstance(data)
        bottomSheet.show(supportFragmentManager, null)
        bottomSheet.setOnClickListener {
            when (it) {
                Action.CONFIRM -> {
                    adapter?.deleteItem(data, position)
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
                binding.mainContent.bottomView.itemIconTintList =
                    AppCompatResources.getColorStateList(this, R.color.white)
                binding.mainContent.bottomView.itemTextColor =
                    AppCompatResources.getColorStateList(this, R.color.white)
            }
            else -> {
                binding.mainContent.bottomView.setBackgroundResource(R.color.white)
                binding.toolbar.visibility = View.VISIBLE
                binding.mainContent.bottomView.itemIconTintList =
                    AppCompatResources.getColorStateList(this, R.color.color_111111)
                binding.mainContent.bottomView.itemTextColor =
                    AppCompatResources.getColorStateList(this, R.color.color_111111)
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
                dismissProgress()
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val list = response.body()
                    list?.toMutableList()?.let { listMenu.addAll(it) }
                    for (i in listMenu.indices) {
                        listMenu[i].isSelected = i == 0
                    }
                    adapter?.notifyDataSetChanged()
                    if (listMenu.size > 0) {
                        workspaceGroupData = listMenu[0]
                        binding.tvTitleToolbar.text = listMenu[0].name
                        workspaceGroupData?.let {
                            administratorFragment.updateDataView(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<WorkspaceGroupData>>, t: Throwable) {
                t.message?.let { Log.e("onFailure: ", it) }
                dismissProgress()
            }
        }))
    }

    private fun doGetUserInfo() {
        if (ViSafeApp().getPreference().getUserInfo().toString().isEmpty()) {
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