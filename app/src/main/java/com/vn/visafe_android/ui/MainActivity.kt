package com.vn.visafe_android.ui

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityMainBinding
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.ui.home.MenuAdapter
import com.vn.visafe_android.ui.home.OnClickMenu

class MainActivity : BaseActivity() {
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
}