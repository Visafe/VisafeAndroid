package com.vn.visafe_android.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityMainBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.home.*
import com.vn.visafe_android.ui.home.administrator.AdministratorFragment

class MainActivity : BaseActivity() {
    companion object {
        const val POSITION_HOME = 0
        const val POSITION_PROTECT = 1
        const val POSITION_SETTING = 2
    }

    private var listFragment = mutableListOf<Fragment>()
    private var currentPosition = 0
    lateinit var binding: ActivityMainBinding
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var adapter: MenuAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
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
    }

    private fun initTab() {
        listFragment.add(POSITION_HOME, AdministratorFragment.newInstance())
        listFragment.add(POSITION_PROTECT, ProtectFragment.newInstance())
        listFragment.add(POSITION_SETTING, SettingFragment.newInstance())

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
        adapter = MenuAdapter(createListMenu(), object : OnClickMenu {
            override fun onClickMenu(data: WorkspaceGroupData, position: Int) {
                adapter?.setSelected(position)
            }

            override fun onMoreGroup() {

            }
        })
        binding.rvGroup.adapter = adapter
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    private fun createListMenu(): List<WorkspaceGroupData> {
        val listMenu: ArrayList<WorkspaceGroupData> = ArrayList()
        listMenu.add(WorkspaceGroupData("NCSC", 5, "Quản trị", false))
        listMenu.add(WorkspaceGroupData("Fina Group", 4, "Thành viên", false))
        listMenu.add(WorkspaceGroupData("Cihub Architect", 2, "Thành viên", false))
        listMenu.add(WorkspaceGroupData("AgileTech", 1, "Thành viên", false))
        listMenu.add(WorkspaceGroupData("Vinfast", 5, "Thành viên", false))
        listMenu.add(WorkspaceGroupData("VinGroup", 5, "Quản trị", false))
        return listMenu
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
}