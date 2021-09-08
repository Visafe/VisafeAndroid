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
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as NotificationModel
        if (content != other.content) return false
        if (createdAt != other.createdAt) return false
        if (group != other.group) return false
        if (id != other.id) return false
        if (isRead != other.isRead) return false
        if (isSee != other.isSee) return false
        return true
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (group?.hashCode() ?: 0)
        result = 31 * result + (id ?: 0)
        result = 31 * result + (isRead?.hashCode() ?: 0)
        result = 31 * result + (isSee?.hashCode() ?: 0)
        return result
    }
}

@Parcelize
data class ContentNotis(
    @SerializedName("affected")
    var affected: AffectedNotis? = null,
    @SerializedName("target")
    var target: TargetNotis? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("duration")
    var duration: String? = null,
    @SerializedName("package_name")
    var package_name: String? = null,
    @SerializedName("status_payment")
    var status_payment: String? = null
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