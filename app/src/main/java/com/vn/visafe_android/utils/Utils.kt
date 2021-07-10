package com.vn.visafe_android.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

object Utils {

    fun sendEmail(activity: Activity, email: String, subject: String?, content: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        try {
            activity.startActivity(Intent.createChooser(intent, "Sending..."))
        } catch (e: ActivityNotFoundException) {
        }
    }

    fun callPhone(phone: String, activity: Activity) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        activity.startActivity(intent)
    }

    fun openWebsite(url: String, activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }
}