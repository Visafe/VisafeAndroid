package com.vn.visafe_android.model.request

import com.google.gson.annotations.SerializedName
import com.vn.visafe_android.model.NotificationModel
import com.vn.visafe_android.model.UsersGroupInfo

data class CreateGroupRequest(
    @SerializedName("ids")//mảng các deviceId trong group
    var ids: List<String>? = listOf(),
    @SerializedName("tags")//không sử dung
    var tags: List<String>? = listOf(),
    @SerializedName("name")//tên group
    var name: String? = null,
    @SerializedName("groupid")//id của group
    var groupid: String? = null,
    @SerializedName("use_global_settings")// không sử dụng
    var use_global_settings: Boolean? = false,
    @SerializedName("filtering_enabled")//bật chế độ lọc
    var filtering_enabled: Boolean? = false,
    @SerializedName("parental_enabled")//chế độ cha mẹ phụ huynh
    var parental_enabled: Boolean? = false,
    @SerializedName("safesearch_enabled")//chế độ tìm kiếm an toàn
    var safesearch_enabled: Boolean? = false,
    @SerializedName("youtuberestrict_enabled")//chế độ hạn chặn nội dụng nhạy cảm trên youtube
    var youtuberestrict_enabled: Boolean? = false,
    @SerializedName("safebrowsing_enabled")//không sử dụng
    var safebrowsing_enabled: Boolean? = false,
    @SerializedName("phishing_enabled")//bật chế độ chặn các trang phishing
    var phishing_enabled: Boolean? = false,
    @SerializedName("malware_enabled")//chặn các trang, app chứa malware
    var malware_enabled: Boolean? = false,
    @SerializedName("gambling_enabled")//Chặn các trang, app cờ bạc
    var gambling_enabled: Boolean? = false,
    @SerializedName("bypass_enabled")//Chế độ chặn VPN, proxy
    var bypass_enabled: Boolean? = false,
    @SerializedName("adblock_enabled")//Chặn quảng cáo
    var adblock_enabled: Boolean? = false,
    @SerializedName("porn_enabled")//Chặn website 18+
    var porn_enabled: Boolean? = false,
    @SerializedName("game_ads_enabled")// Chặn quảng cáo game
    var game_ads_enabled: Boolean? = false,
    @SerializedName("log_enabled")//Bật tắt ghi log
    var log_enabled: Boolean? = false,
    @SerializedName("use_global_blocked_services")//không sử dụng
    var use_global_blocked_services: Boolean? = false,
    @SerializedName("blocked_services")//Mảng chứa các ứng dụng cần chặn
    var blocked_services: List<String>? = listOf(),
    @SerializedName("native_tracking")//Chặn theo dõi các ứng dụng
    var native_tracking: List<String>? = listOf(),
    @SerializedName("app_ads")//Chặn quảng cáo ứng dụng
    var app_ads: List<String>? = listOf(),
    @SerializedName("object_type")//Thể loại của Group
    var object_type: List<String>? = listOf(),
    @SerializedName("block_webs")//Chặn các website trong mảng
    var block_webs: List<String>? = listOf(),
    @SerializedName("workspace_id")
    var workspace_id: String? = null,
    @SerializedName("upstreams")//không sử dụng
    var upstreams: List<String>? = listOf(),
    @SerializedName("fkUserId")//ID người dùng tạo group
    var fkUserId: Int? = null,
    @SerializedName("isOwner")//Người dùng hiện tại get group có phải owner hay k
    var isOwner: Boolean? = false,
    /* @SerializedName("whois_info")
     var whois_info: List<String>? = listOf(),*/
    @SerializedName("disallowed")//không sử dụng
    var disallowed: Boolean? = null,
    @SerializedName("disallowed_rule")//không sử dụng
    var disallowed_rule: Boolean? = null,
    @SerializedName("usersActive")//Mảng chứa ID người dùng có role là giám sát viên trong group
    var usersActive: List<String>? = listOf(),
    @SerializedName("userManage")//Mảng chứa ID người dùng có role là quản trị viên viên trong group
    var userManage: List<String>? = listOf(),
    @SerializedName("identifiers")//Mảng chứa ID người định danh trong group
    var identifiers: List<String>? = listOf(),
    @SerializedName("usersGroupInfo")// Mảng chứa các thông tin user trong group
    var listUsersGroupInfo: List<UsersGroupInfo>? = null,
    @SerializedName("devicesGroupInfo")//Mảng chứa các thông tin device trong group
    var listDevicesGroupInfo: List<String>? = null,
    @SerializedName("identifiersGroupInfo")//Mảng chứa các thông tin người định danh trong group
    var identifiersGroupInfo: List<String>? = null,
    @SerializedName("createdAt")//thời gian tạo
    var createdAt: String? = null,
    @SerializedName("notifications")//3 notification cảnh báo mới nhất của group (Cảnh báo truy cập domain phishing, malware ...),
    var notifications: List<NotificationModel>? = null,
    @SerializedName("times")//Đặt thời gian filter trong ngày
    var times: List<TimesGroup>? = listOf(/*TimesGroup(TimeItem(12, 13), TimeItem(12, 13), true), TimesGroup(TimeItem(12, 13), TimeItem(12, 13), true)*/),
    @SerializedName("days")//Chạy filter các ngày lặp lại trong tuần: 0 - Chủ nhật, 1- Thứ 2 ... 6 - Thứ 7
    var days: List<String>? = listOf(/*"0", "1", "2", "4"*/),
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