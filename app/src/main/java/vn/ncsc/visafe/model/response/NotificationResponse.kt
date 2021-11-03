package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName
import vn.ncsc.visafe.model.NotificationModel

data class NotificationResponse(
    @SerializedName("notis")
    var notis: List<NotificationModel>? = null,
    @SerializedName("count")
    var count: Int? = null
)