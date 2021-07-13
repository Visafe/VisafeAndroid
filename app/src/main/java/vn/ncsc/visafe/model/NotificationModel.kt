package vn.ncsc.visafe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationModel(
    @SerializedName("content")
    var content: ContentNotis? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("group")
    var group: GroupNotis? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("isRead")
    var isRead: Boolean? = false,
    @SerializedName("isSee")
    var isSee: Boolean? = false
) : Parcelable

@Parcelize
data class ContentNotis(
    @SerializedName("affected")
    var affected: AffectedNotis? = null,
    @SerializedName("target")
    var target: TargetNotis? = null,
    @SerializedName("type")
    var type: String? = null
) : Parcelable

@Parcelize
data class AffectedNotis(
    @SerializedName("deviceId")
    var deviceId: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("userID")
    var userID: Int? = null
) : Parcelable

@Parcelize
data class TargetNotis(
    @SerializedName("domain")
    var domain: String? = null,
    @SerializedName("type")
    var type: String? = null
) : Parcelable

@Parcelize
data class GroupNotis(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null
) : Parcelable