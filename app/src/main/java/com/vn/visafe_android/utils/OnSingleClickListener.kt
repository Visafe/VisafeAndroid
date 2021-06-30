package com.vn.visafe_android.utils

import android.os.SystemClock
import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {
    companion object {
        const val MIN_CLICK_INTERVAL = 500L
    }

    var mLastClickTime = 0

    abstract fun onSingleClick(view: View)

    override fun onClick(v: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime.toInt()

        if (elapsedTime <= MIN_CLICK_INTERVAL) return

        onSingleClick(v!!)
    }
}