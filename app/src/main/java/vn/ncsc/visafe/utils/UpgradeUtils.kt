package vn.ncsc.visafe.utils

import android.content.Context
import vn.ncsc.visafe.R

fun premiumList(context: Context): List<String> {
    val strings = ArrayList<String>()
    strings.add(context.getString(R.string.protect_device))
    strings.add(context.getString(R.string.protect_wifi))
    strings.add(context.getString(R.string.chan_khong_gioi_han))
    strings.add(context.getString(R.string.phan_tich_bao_cao))
    strings.add(context.getString(R.string.max_3_device))
    strings.add(context.getString(R.string.management_1_group))
    strings.add(context.getString(R.string.support_call_chat))
    return strings
}

fun familyList(context: Context): List<String> {
    val strings = ArrayList<String>()
    strings.add(context.getString(R.string.protect_device))
    strings.add(context.getString(R.string.protect_wifi))
    strings.add(context.getString(R.string.chan_khong_gioi_han))
    strings.add(context.getString(R.string.phan_tich_bao_cao))
    strings.add(context.getString(R.string.max_9_device))
    strings.add(context.getString(R.string.management_1_group))
    strings.add(context.getString(R.string.support_call_chat))
    return strings
}

fun businessList(context: Context): List<String> {
    val strings = ArrayList<String>()
    strings.add(context.getString(R.string.protect_device))
    strings.add(context.getString(R.string.protect_wifi))
    strings.add(context.getString(R.string.chan_khong_gioi_han))
    strings.add(context.getString(R.string.phan_tich_bao_cao))
    strings.add(context.getString(R.string.max_device))
    strings.add(context.getString(R.string.management_group))
    strings.add(context.getString(R.string.support_call_chat_24))
    return strings
}