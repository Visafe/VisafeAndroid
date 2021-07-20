package vn.ncsc.visafe.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
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
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.ui.adapter.TimeStatistical
import vn.ncsc.visafe.ui.home.*
import vn.ncsc.visafe.utils.PreferenceKey
import vn.ncsc.visafe.utils.SharePreferenceKeyHelper
import vn.ncsc.visafe.utils.setBackgroundTint


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
    private var listWorkSpace: MutableList<WorkspaceGroupData> = mutableListOf()
    private var currentPosition = 0
    private var overViewProtectFragment = OverViewProtectFragment()
    private var groupManagementFragment = GroupManagementFragment()
    private var notificationFragment = NotificationFragment()
    private var profileFragment = ProfileFragment()

    var user: MutableLiveData<UserInfo> = MutableLiveData()
    var listWorkSpaceLiveData: MutableLiveData<List<WorkspaceGroupData>> = MutableLiveData()
    var statisticalWorkSpaceLiveData: MutableLiveData<StatsWorkSpace> = MutableLiveData()
    var timeTypes: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        doGetWorkSpaces()
        doGetUserInfo()
    }

    private fun initView() {
        timeTypes.value = TimeStatistical.values()[0].time
        initTab()
    }

    private fun initTab() {
        listFragment.add(POSITION_PROTECT, overViewProtectFragment)
        listFragment.add(POSITION_GROUP, groupManagementFragment)
        listFragment.add(POSITION_SCAN, HomeFragment())
        listFragment.add(POSITION_NOTIFICATION, notificationFragment)
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
        when (item.itemId) {
            R.id.navigation_protect -> {
                openTab(POSITION_PROTECT)
                return true
            }
            R.id.navigation_group -> {
                if (needLogin()) {
                    return false
                }
                openTab(POSITION_GROUP)
                return true
            }
            R.id.navigation_scan -> {
                openTab(POSITION_SCAN)
                return true
            }
            R.id.navigation_notification -> {
                if (needLogin()) {
                    return false
                }
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
                    response.body()?.toMutableList()?.let {
                        listWorkSpace.clear()
                        listWorkSpace.addAll(it)
                        listWorkSpaceLiveData.value = listWorkSpace
                        if (listWorkSpace.size > 0) {
                            for (i in listWorkSpace.indices) {
                                listWorkSpace[i].isSelected = i == 0
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