package com.vn.visafe_android.ui.create.group

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivityCreateGroupBinding
import com.vn.visafe_android.ui.create.group.protected_group.ProtectedGroupFragment

class CreateGroupActivity : BaseActivity() {
    lateinit var binding: ActivityCreateGroupBinding

    private var totalStep = 4

    private var step = 0
    var somethingObject = CreateGroup()

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

    data class CreateGroup(
        var groupName: String = "",
        var typeChild: Boolean = false,
        var typePeople: Boolean = false,
        var typeOldPeople: Boolean = false,
        var chanQuangCaoWeb: Boolean = false,
        var chanQuangCaoGame: Boolean = false,
        var chanQuangCaoInsta: Boolean = false,
        var chanQuangCaoYoutube: Boolean = false,
        var chanQuangCaoSpotify: Boolean = false,
        var chanQuangCaoFacebook: Boolean = false,
    )
}