package com.vn.visafe_android.model.request

import com.google.gson.annotations.SerializedName

data class CreateGroupRequest(
    @SerializedName("adblock_enabled")
    var adblock_enabled: Boolean? = false,
    @SerializedName("app_ads")
    var app_ads: List<String>? = listOf(),
    @SerializedName("block_webs")
    var block_webs: List<String>? = listOf(),
    @SerializedName("blocked_services")
    var blocked_services: List<String>? = listOf(),
    @SerializedName("bypass_enabled")
    var bypass_enabled: Boolean? = false,
    @SerializedName("gambling_enabled")
    var gambling_enabled: Boolean? = false,
    @SerializedName("game_ads_enabled")
    var game_ads_enabled: Boolean? = false,
    @SerializedName("malware_enabled")
    var malware_enabled: Boolean? = false,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("native_tracking")
    var native_tracking: List<String>? = listOf(),
    @SerializedName("object_type")
    var object_type: List<String>? = listOf(),
    @SerializedName("parental_enabled")
    var parental_enabled: Boolean? = false,
    @SerializedName("phishing_enabled")
    var phishing_enabled: Boolean? = false,
    @SerializedName("porn_enabled")
    var porn_enabled: Boolean? = false,
    @SerializedName("safesearch_enabled")
    var safesearch_enabled: Boolean? = false,
    @SerializedName("youtuberestrict_enabled")
    var youtuberestrict_enabled: Boolean? = false,
    @SerializedName("workspace_id")
    var workspace_id: String? = null,
    @SerializedName("times")
    var times: List<TimesGroup>? = listOf(TimesGroup(TimeItem(12, 13), TimeItem(12, 13), true), TimesGroup(TimeItem(12, 13), TimeItem(12, 13), true)),
    @SerializedName("days")
    var days: List<String>? = listOf("0", "1", "2", "4"),
)

data class TimesGroup(
    @SerializedName("start")
    var start: TimeItem? = null,
    @SerializedName("end")
    var end: TimeItem? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = false,
)

data class TimeItem(
    @SerializedName("hour")
    var hour: Int? = 0,
    @SerializedName("end")
    var minutes: Int? = 0
)