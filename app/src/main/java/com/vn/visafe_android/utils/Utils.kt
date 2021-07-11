package com.vn.visafe_android.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log

object Utils {

    fun sendEmail(activity: Activity, email: String, subject: String?) {
        try {
            activity.startActivity(
                Intent(Intent.ACTION_SENDTO).apply {
                    data = (Uri.parse("mailto:$email"))
                        .buildUpon()
                        .appendQueryParameter(
                            "subject", subject
                        ).appendQueryParameter(
                            "to", email
                        )
                        .build()
                }
            )
        } catch (e: Exception) {
            Log.e("sendEmail", "failed to open mail client")
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