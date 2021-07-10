package com.vn.visafe_android.utils

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.vn.visafe_android.R

fun isValidEmail(target: CharSequence): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

fun updateOTPCode(
    etPin: EditText,
    tvOtp1: TextView,
    tvOtp2: TextView,
    tvOtp3: TextView,
    tvOtp4: TextView,
    tvOtp5: TextView,
    tvOtp6: TextView,
    context: Context
) {
    tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    when (etPin.length()) {
        1 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        2 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        3 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        4 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        5 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        6 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
        }
    }
}