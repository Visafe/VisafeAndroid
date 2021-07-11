package com.vn.visafe_android.utils

import android.content.Context
import android.text.format.DateUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.vn.visafe_android.R
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*

fun isValidEmail(target: CharSequence): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

fun isNumber(input: String): Boolean {
    val integerChars = '0'..'9'
    var dotOccurred = 0
    return input.all { it in integerChars || it == '.' && dotOccurred++ < 1 }
}

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS


fun getTimeAgo(createAt: Long): String? {
    var time = createAt
    if (time < 1000000000000L) {
        // if timestamp given in seconds, convert to millis
        time *= 1000
    }
    val now: Long = currentTimeMillis()
    if (time > now || time <= 0) {
        return null
    }
    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> {
            "Vừa xong"
        }
        diff < 2 * MINUTE_MILLIS -> {
            "1 phút trước"
        }
        diff < 50 * MINUTE_MILLIS -> {
            (diff / MINUTE_MILLIS).toString() + " phút trước"
        }
        diff < 90 * MINUTE_MILLIS -> {
            "1 giờ trước"
        }
        diff < 24 * HOUR_MILLIS -> {
            (diff / HOUR_MILLIS).toString() + " giờ trước"
        }
        diff < 48 * HOUR_MILLIS -> {
            "hôm qua"
        }
        else -> {
            (diff / DAY_MILLIS).toString() + " ngày trước"
        }
    }
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
    tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_edittext_focus)
    tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
    when (etPin.length()) {
        1 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_edittext_focus)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        2 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_edittext_focus)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        3 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.ic_edittext_focus)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        4 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.ic_edittext_focus)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.bg_otp)
        }
        5 -> {
            tvOtp1.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp2.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp3.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp4.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp5.background = ContextCompat.getDrawable(context, R.drawable.ic_otp_dot)
            tvOtp6.background = ContextCompat.getDrawable(context, R.drawable.ic_edittext_focus)
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

fun getTextGroup(text: String?): String? {
    return if (text == null || "" == text) {
        ""
    } else {
        var textDefault = text.replace("[/!@#$%^&*(),\\-+=]".toRegex(), "").replace("  ", " ")
        textDefault = textDefault.replace("-".toRegex(), "")
        textDefault = textDefault.replace(".", " ")
        textDefault = textDefault.replace("  ", " ")
        textDefault = textDefault.trim { it <= ' ' }
        val str = textDefault.split(" ".toRegex()).toTypedArray()
        textDefault = if (str.size > 2) {
            if (str[0].trim { it <= ' ' }.isNotEmpty() && str[2].trim { it <= ' ' }.isNotEmpty()) {
                str[0].trim { it <= ' ' }.substring(0, 1) + str[2].trim { it <= ' ' }.substring(0, 1)
            } else {
                ""
            }
        } else if (str.size > 1) {
            if (str[0].trim { it <= ' ' }.isNotEmpty() && str[1].trim { it <= ' ' }.isNotEmpty()) {
                str[0].trim { it <= ' ' }.substring(0, 1) + str[1].trim { it <= ' ' }.substring(0, 1)
            } else {
                ""
            }
        } else if (str.isNotEmpty()) {
            if (str[0].trim { it <= ' ' }.isNotEmpty()) {
                str[0].trim { it <= ' ' }.substring(0, 1)
            } else {
                ""
            }
        } else {
            ""
        }
        textDefault = textDefault.uppercase(Locale.getDefault())
        textDefault
    }
}