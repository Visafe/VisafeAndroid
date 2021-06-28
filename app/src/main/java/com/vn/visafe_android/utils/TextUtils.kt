package com.vn.visafe_android.utils

import android.util.Patterns


fun isValidEmail(target: CharSequence): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(target).matches()
}