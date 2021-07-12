package com.vn.visafe_android.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

fun validatePhone(phone: String?): Boolean {
    return try {
        var regex: String
        var matcher: Matcher
        regex = "^84[35789][0-9]{8}$"
        matcher = Pattern.compile(regex).matcher(phone)
        if (matcher.find()) {
            return true
        }
        regex = "^0[35789][0-9]{8}$"
        matcher = Pattern.compile(regex).matcher(phone)
        if (matcher.find()) {
            return true
        }
        regex = "^84[0-9]{8}$"
        matcher = Pattern.compile(regex).matcher(phone)
        if (!matcher.find()) {
            regex = "^[89][0-9]{8}$"
            matcher = Pattern.compile(regex).matcher(phone)
            if (matcher.find()) {
                return true
            }
        }
        regex = "^[35789][0-9]{8}$"
        matcher = Pattern.compile(regex).matcher(phone)
        matcher.find()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun formatMobileHead84(phone: String): String? {
    var msisdn = phone
    var strResult: String? = ""
    if (msisdn != null && "" != msisdn) {
        msisdn = msisdn.trim { it <= ' ' }
        msisdn = setPhoneNumber(msisdn)
        strResult = when {
            msisdn?.startsWith("0") -> {
                msisdn?.replaceFirst("0".toRegex(), "84")
            }
            msisdn?.startsWith("84") -> {
                msisdn
            }
            else -> {
                "84$msisdn"
            }
        }
    }
    return strResult
}

private fun setPhoneNumber(phone: String): String {
    var phoneNumber = phone
    phoneNumber = phoneNumber.replace(" ", "")
    phoneNumber = phoneNumber.replace("+", "")
    phoneNumber = phoneNumber.replace("-", "")
    phoneNumber = phoneNumber.replace("(", "")
    phoneNumber = phoneNumber.replace(")", "")
    phoneNumber = phoneNumber.replace("/", "")
    phoneNumber = phoneNumber.replace("*", "")
    phoneNumber = phoneNumber.replace(",", "")
    phoneNumber = phoneNumber.replace("#", "")
    phoneNumber = phoneNumber.replace(";", "")
    phoneNumber = phoneNumber.replace(".", "")
    phoneNumber = phoneNumber.replace("\\|", "")
    return phoneNumber
}