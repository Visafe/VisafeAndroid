package com.vn.visafe_android.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager(
    context: Context,
    attrs: AttributeSet
) : ViewPager(context, attrs) {

    var swipeable: Boolean = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (swipeable) {
            return super.onTouchEvent(ev)
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (swipeable) {
            return super.onInterceptTouchEvent(ev)
        }
        return false
    }
}