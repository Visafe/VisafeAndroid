package com.vn.visafe_android.ui.authentication.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.vn.visafe_android.R
import com.vn.visafe_android.base.BaseActivity
import com.vn.visafe_android.databinding.ActivitySplashBinding
import com.vn.visafe_android.ui.MainActivity
import com.vn.visafe_android.ui.adapter.SectionsPagerAdapter
import com.vn.visafe_android.ui.authentication.LoginActivity
import com.vn.visafe_android.utils.PreferenceKey
import com.vn.visafe_android.utils.SharePreferenceKeyHelper
import com.vn.visafe_android.utils.setOnSingClickListener

class SplashActivity : BaseActivity() {

    lateinit var viewBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (SharePreferenceKeyHelper.getInstance(application).isLogin()
            || SharePreferenceKeyHelper.getInstance(application).isFirstShowOnBoarding()
        ) {
            viewBinding.imgLogo.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 2000)
        } else {
            viewBinding.imgLogo.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                initView()
            }, 2000)
        }
    }

    private fun initView() {
        viewBinding.imgLogo.visibility = View.GONE
        viewBinding.tabs.visibility = View.VISIBLE
        viewBinding.fab.visibility = View.VISIBLE
        viewBinding.viewPager.visibility = View.VISIBLE
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewBinding.viewPager.adapter = sectionsPagerAdapter

        viewBinding.fab.setOnSingClickListener {
            if (viewBinding.fab.text.equals(getString(R.string.start))) {
                SharePreferenceKeyHelper.getInstance(application).putBoolean(PreferenceKey.IS_FIRST_SHOW_ON_BOARDING, true)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                val currentItem = viewBinding.viewPager.currentItem
                viewBinding.viewPager.currentItem = currentItem + 1
            }
        }

        viewBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        viewBinding.tab1.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                        viewBinding.tab2.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab3.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.fab.text = getString(R.string.next)
                    }
                    1 -> {
                        viewBinding.tab1.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab2.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                        viewBinding.tab3.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.fab.text = getString(R.string.next)
                    }
                    2 -> {
                        viewBinding.tab1.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab2.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_inactive)
                        viewBinding.tab3.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.bg_active)
                        viewBinding.fab.text = getString(R.string.start)

                    }

                    else -> { // Note the block
                        0
                    }
                }

            }

        })
    }
}