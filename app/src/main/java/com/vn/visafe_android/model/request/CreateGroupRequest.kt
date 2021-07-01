package com.vn.visafe_android.model.request

import com.google.gson.annotations.SerializedName

data class CreateGroupRequest(
    @SerializedName("adblock_enabled")
    var adblock_enabled: Boolean? = null,
    @SerializedName("app_ads")
    var app_ads: List<String>? = listOf(),
    @SerializedName("block_webs")
    var block_webs: List<String>? = listOf(),
    @SerializedName("blocked_services")
    var blocked_services: List<String>? = listOf(),
    @SerializedName("bypass_enabled")
    var bypass_enabled: Boolean? = null,
    @SerializedName("gambling_enabled")
    var gambling_enabled: Boolean? = null,
    @SerializedName("game_ads_enabled")
    var game_ads_enabled: Boolean? = null,
    @SerializedName("malware_enabled")
    var malware_enabled: Boolean? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("native_tracking")
    var native_tracking: List<String>? = listOf(),
    @SerializedName("object_type")
    var object_type: List<String>? = listOf(),
    @SerializedName("parental_enabled")
    var parental_enabled: Boolean? = null,
    @SerializedName("phishing_enabled")
    var phishing_enabled: Boolean? = null,
    @SerializedName("porn_enabled")
    var porn_enabled: Boolean? = null,
    @SerializedName("safesearch_enabled")
    var safesearch_enabled: Boolean? = null,
    @SerializedName("youtuberestrict_enabled")
    var youtuberestrict_enabled: Boolean? = null,
    @SerializedName("workspace_id")
    var workspace_id: String? = null
)