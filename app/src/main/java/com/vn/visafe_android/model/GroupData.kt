package com.vn.visafe_android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import com.google.gson.annotations.SerializedName
import com.vn.visafe_android.model.response.DeviceGroup

@Parcelize
data class GroupData(
    @SerializedName("ids")
    var ids: List<String>? = listOf(),
    @SerializedName("tags")
    var tags: List<String>? = listOf(),
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("groupid")
    var groupid: String? = null,
    @SerializedName("use_global_settings")
    var use_global_settings: Boolean? = false,
    @SerializedName("filtering_enabled")
    var filtering_enabled: Boolean? = false,
    @SerializedName("parental_enabled")
    var parental_enabled: Boolean? = false,
    @SerializedName("safesearch_enabled")
    var safesearch_enabled: Boolean? = false,
    @SerializedName("youtuberestrict_enabled")
    var youtuberestrict_enabled: Boolean? = false,
    @SerializedName("safebrowsing_enabled")
    var safebrowsing_enabled: Boolean? = false,
    @SerializedName("phishing_enabled")
    var phishing_enabled: Boolean? = false,
    @SerializedName("malware_enabled")
    var malware_enabled: Boolean? = false,
    @SerializedName("gambling_enabled")
    var gambling_enabled: Boolean? = false,
    @SerializedName("bypass_enabled")
    var bypass_enabled: Boolean? = false,
    @SerializedName("adblock_enabled")
    var adblock_enabled: Boolean? = false,
    @SerializedName("porn_enabled")
    var porn_enabled: Boolean? = false,
    @SerializedName("game_ads_enabled")
    var game_ads_enabled: Boolean? = false,
    @SerializedName("log_enabled")
    var log_enabled: Boolean? = false,
    @SerializedName("use_global_blocked_services")
    var use_global_blocked_services: Boolean? = false,
    @SerializedName("blocked_services")
    var blocked_services: List<String>? = listOf(),
    @SerializedName("native_tracking")
    var native_tracking: List<String>? = listOf(),
    @SerializedName("app_ads")
    var app_ads: List<String>? = listOf(),
    @SerializedName("object_type")
    var object_type: List<String>? = listOf(),
    @SerializedName("block_webs")
    var block_webs: List<String>? = listOf(),
    @SerializedName("workspace_id")
    var workspace_id: String? = null,
    @SerializedName("upstreams")
    var upstreams: List<String>? = listOf(),
    @SerializedName("fkUserId")
    var fkUserId: Int? = null,
    @SerializedName("isOwner")
    var isOwner: Boolean? = false,
    /* @SerializedName("whois_info")
     var whois_info: List<String>? = listOf(),*/
    @SerializedName("disallowed")
    var disallowed: Boolean? = null,
    @SerializedName("disallowed_rule")
    var disallowed_rule: Boolean? = null,
    @SerializedName("usersActive")
    var usersActive: List<String>? = listOf(),
    @SerializedName("userManage")
    var userManage: List<String>? = listOf(),
    @SerializedName("identifiers")
    var identifiers: List<String>? = listOf(),
    @SerializedName("usersGroupInfo")
    var listUsersGroupInfo: List<UsersGroupInfo>? = null,
    @SerializedName("devicesGroupInfo")
    var listDevicesGroupInfo: List<DeviceGroup>? = null,
    @SerializedName("identifiersGroupInfo")
    var identifiersGroupInfo: List<String>? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("notifications")
    var notifications: List<NotificationModel>? = null,
    @SerializedName("times")
    var times: List<TimesGroup>? = null,
    @SerializedName("days")
    var days: List<String>? = null,
) : Parcelable

@Parcelize
data class TimesGroup(
    @SerializedName("start")
    var start: TimeItem? = null,
    @SerializedName("end")
    var end: TimeItem? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = false,
) : Parcelable

@Parcelize
data class TimeItem(
    @SerializedName("hour")
    var hour: Int? = 0,
    @SerializedName("end")
    var minutes: Int? = 0
) : Parcelable

@Parcelize
data class UsersGroupInfo(
    @SerializedName("UserID")
    var userID: Int? = null,
    @SerializedName("FullName")
    var fullName: String? = null,
    @SerializedName("Email")
    var email: String? = null,
    @SerializedName("PhoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("IsVerify")
    var isVerify: Boolean? = null,
) : Parcelable