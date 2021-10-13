package vn.ncsc.visafe.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.ncsc.visafe.R
import vn.ncsc.visafe.ViSafeApp
import vn.ncsc.visafe.base.BaseActivity
import vn.ncsc.visafe.data.BaseCallback
import vn.ncsc.visafe.data.NetworkClient
import vn.ncsc.visafe.databinding.ActivityMainBinding
import vn.ncsc.visafe.model.StatsWorkSpace
import vn.ncsc.visafe.model.UserInfo
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.model.request.SendTokenRequest
import vn.ncsc.visafe.model.response.CheckDeviceInGroupResponse
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.authentication.LoginActivity
import vn.ncsc.visafe.ui.authentication.splash.SplashActivity
import vn.ncsc.visafe.ui.group.join.JoinGroupActivity
import vn.ncsc.visafe.ui.home.*
import vn.ncsc.visafe.utils.EventUtils
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.utils.setBackgroundTint
import java.util.*


class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val POSITION_PROTECT = 0
        const val POSITION_GROUP = 1
        const val POSITION_SCAN = 2
        const val POSITION_NOTIFICATION = 3
        const val POSITION_PROFILE = 4
        const val IS_FIRST_CREATE_WORKSPACE = "IS_FIRST_CREATE_WORKSPACE"
        const val MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1
    }

    lateinit var binding: ActivityMainBinding

    private var wifiManager: WifiManager? = null

    private var listFragment = mutableListOf<Fragment>()
    private var listWorkSpace: MutableList<WorkspaceGroupData> = mutableListOf()
    private var currentPosition = 0

    var mCurrentScreen: Int = POSITION_SCAN
    var userInfoLiveData: MutableLiveData<UserInfo> = MutableLiveData()
    var listWorkSpaceLiveData: MutableLiveData<List<WorkspaceGroupData>> = MutableLiveData()
    var statisticalWorkSpaceLiveData: MutableLiveData<StatsWorkSpace> = MutableLiveData()
    var timeTypes: MutableLiveData<String> = MutableLiveData()
    var timeScanUpdate: MutableLiveData<String> = MutableLiveData()
    var isOpenProtectedDevice: MutableLiveData<Boolean> = MutableLiveData()
    var isLoadView: MutableLiveData<Boolean> = MutableLiveData()
    private var isLoadUserInfo = false

    private var timer: Timer? = null
    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            if (SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.TIME_LAST_SCAN).isNotEmpty()) {
                timeScanUpdate.postValue(
                    SharePreferenceKeyHelper.getInstance(ViSafeApp()).getString(PreferenceKey.TIME_LAST_SCAN)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

    override fun onResume() {
        super.onResume()
        if (timer != null) {
            return
        }
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask, 0, 2000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_ACCESS_COARSE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                wifiManager?.startScan()
            } else {
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissionWifi()
        getNewTokenFCM()
        intent?.let {
            val groupId = it.getStringExtra(JoinGroupActivity.GROUP_ID)
            val groupName = it.getStringExtra(JoinGroupActivity.GROUP_NAME)
            isLoadUserInfo = it.getBooleanExtra(SplashActivity.LOAD_USER_INFO, false)
            if (groupId?.isNotEmpty() == true && groupName?.isNotEmpty() == true) {
                val intent = Intent(this@MainActivity, JoinGroupActivity::class.java)
                intent.putExtra(JoinGroupActivity.GROUP_ID, groupId)
                intent.putExtra(JoinGroupActivity.GROUP_NAME, groupName)
                startActivity(intent)
            }
        }
        initView()
        doGetUserInfo()
    }

    private var resultLauncherLoginActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                checkPermissionWifi()
                getNewTokenFCM()
//                initView()
                doGetUserInfo()
                isLoadView.value = true
            }
        }

    fun needLogin(currentScreen: Int): Boolean {
        if (SharePreferenceKeyHelper.getInstance(application).isLogin())
            return false
        resultLauncherLoginActivity.launch(Intent(this, LoginActivity::class.java))
        mCurrentScreen = currentScreen
        return true
    }

    private fun initView() {
        timeTypes.value = TimeStatistical.values()[0].time
        val pin = ViSafeApp().getPreference().getString(PreferenceKey.PIN_CODE) ?: ""
        EventUtils.isCreatePass.value = pin.isNotEmpty()
        initTab()
    }

    private fun initTab() {
        listFragment.clear()
        listFragment.add(POSITION_PROTECT, OverViewProtectFragment())
        listFragment.add(POSITION_GROUP, GroupManagementFragment())
        listFragment.add(POSITION_SCAN, HomeFragment())
        listFragment.add(POSITION_NOTIFICATION, NotificationFragment())
        listFragment.add(POSITION_PROFILE, ProfileFragment())
        if (isLoadUserInfo) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            listFragment.forEachIndexed { index, fragment ->
                fragmentTransaction.add(R.id.fr_container, fragment, fragment.javaClass.simpleName)
                if (index == POSITION_PROFILE) {
                    fragmentTransaction.show(fragment)
                } else {
                    fragmentTransaction.hide(fragment)
                }
            }
            fragmentTransaction.commitAllowingStateLoss()
            openTab(POSITION_PROFILE)
        } else {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            listFragment.forEachIndexed { index, fragment ->
                fragmentTransaction.add(R.id.fr_container, fragment, fragment.javaClass.simpleName)
                if (index == mCurrentScreen) {
                    fragmentTransaction.show(fragment)
                } else {
                    fragmentTransaction.hide(fragment)
                }
            }
            fragmentTransaction.commitAllowingStateLoss()
            openTab(mCurrentScreen)
        }
        binding.bottomView.setOnNavigationItemSelectedListener(this)
    }

    fun doGetUserInfo() {
        if (!SharePreferenceKeyHelper.getInstance(application).isLogin())
            return
        if (ViSafeApp().getPreference().getUserInfo().userID == null || isLoadUserInfo) {
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetUserInfo()
            call.enqueue(BaseCallback(this@MainActivity, object : Callback<UserInfo> {
                override fun onResponse(
                    call: Call<UserInfo>,
                    response: Response<UserInfo>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        val gson = Gson()
                        val userInfo = response.body()
                        userInfo?.let {
                            userInfoLiveData.value = it
                        }
                        SharePreferenceKeyHelper.getInstance(application).putString(
                            PreferenceKey.USER_INFO,
                            gson.toJson(userInfo)
                        )
                        doGetWorkSpaces()
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
                userInfoLiveData.value = it
                showProgressDialog()
                doGetWorkSpaces()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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

    fun openWorkspaceTab() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fr_container)
        val newFragment = supportFragmentManager.findFragmentByTag(GroupManagementFragment().javaClass.simpleName)
        doGetWorkSpaces()
        binding.bottomView.menu.getItem(POSITION_GROUP).isChecked = true
        currentFragment?.let { newFragment?.let { it1 -> fragmentTransaction.show(it1).hide(it).commit() } }
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

    private fun doGetWorkSpaces() {
        if (!SharePreferenceKeyHelper.getInstance(application).isLogin())
            return
        val client = NetworkClient()
        val call = client.client(context = applicationContext).doGetWorkSpacesOfCurrentUser()
        call.enqueue(BaseCallback(this@MainActivity, object : Callback<List<WorkspaceGroupData>> {
            override fun onResponse(
                call: Call<List<WorkspaceGroupData>>,
                response: Response<List<WorkspaceGroupData>>
            ) {
                if (response.code() == NetworkClient.CODE_SUCCESS) {
                    val userInfo = SharePreferenceKeyHelper.getInstance(application).getUserInfo()
                    response.body()?.toMutableList()?.let {
                        listWorkSpace.clear()
                        if (it.size > 0) {
                            for (i in it.indices) {
                                if (it[i].id == userInfo.DefaultWorkspace) {
                                    listWorkSpace.add(0, it[i])
                                } else {
                                    listWorkSpace.add(it[i])
                                }
                                listWorkSpace[i].isSelected = i == 0
                            }
                        }
                        listWorkSpaceLiveData.value = listWorkSpace
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

    fun doGetStaticWorkspace(workspaceGroupData: WorkspaceGroupData, timeLimit: String) {
        workspaceGroupData.id.let {
            if (!isLogin())
                return
            showProgressDialog()
            val client = NetworkClient()
            val call = client.client(context = applicationContext).doGetStatisticalOneWorkspace(workspaceGroupData.id, timeLimit)
            call.enqueue(BaseCallback(this, object : Callback<StatsWorkspaceResponse> {
                override fun onResponse(
                    call: Call<StatsWorkspaceResponse>,
                    response: Response<StatsWorkspaceResponse>
                ) {
                    if (response.code() == NetworkClient.CODE_SUCCESS) {
                        response.body()?.let {
                            statisticalWorkSpaceLiveData.value = StatsWorkSpace(it)
                        }

                    }
                    dismissProgress()
                }

                override fun onFailure(call: Call<StatsWorkspaceResponse>, t: Throwable) {
                    t.message?.let { Log.e("onFailure: ", it) }
                    dismissProgress()
                }
            }))
        }

    }

}