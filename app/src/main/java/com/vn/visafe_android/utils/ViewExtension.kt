package com.vn.visafe_android.utils

import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources

fun View.setBackgroundTint(@ColorRes color: Int) {
    backgroundTintList =
        AppCompatResources.getColorStateList(context, color)
}