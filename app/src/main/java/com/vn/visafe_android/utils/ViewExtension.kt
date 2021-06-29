package com.vn.visafe_android.utils

import android.content.res.Resources
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources

fun View.setBackgroundTint(@ColorRes color: Int) {
    backgroundTintList =
        AppCompatResources.getColorStateList(context, color)
}

fun screenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}