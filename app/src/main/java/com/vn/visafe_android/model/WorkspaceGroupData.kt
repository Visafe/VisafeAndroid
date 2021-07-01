package com.vn.visafe_android.model

import android.graphics.Color
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.vn.visafe_android.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkspaceGroupData(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("userOwner")
    var userOwner: Int? = null,
    @SerializedName("isOwner")
    var isOwner: Boolean? = null,
    @SerializedName("phishingEnabled")
    var phishingEnabled: Boolean? = null,
    @SerializedName("malwareEnabled")
    var malwareEnabled: Boolean? = null,
    @SerializedName("logEnabled")
    var logEnabled: Boolean? = null,
    @SerializedName("groupIds")
    var groupIds: List<String>? = null,
    @SerializedName("members")
    var members: List<String>? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    var isSelected: Boolean? = null
) : Parcelable

enum class OWNER(val title: Int, val textColor: Int, val backgroundColor: Int) {
    QUAN_TRI(R.string.quan_tri, Color.parseColor("#FFB31F"), R.color.color_1AFFB31F),
    THANH_VIEN(R.string.thanh_vien, Color.parseColor("#33B6FF"), R.color.color_1A33B6FF);

    companion object {
        fun fromIsOwner(isOwner: Boolean?) = if (isOwner == true) QUAN_TRI else THANH_VIEN
    }
}

enum class TYPE_WORKSPACES(val type: String, val resDrawableIcon: Int) {
    PERSONAL("PERSONAL", R.drawable.ic_personal),
    FAMILY("FAMILY", R.drawable.ic_family),
    ENTERPRISE("ENTERPRISE", R.drawable.ic_business),
    SCHOOL("SCHOOL", R.drawable.ic_education),
    GOVERNMENT_ORGANIZATION("GOVERNMENT_ORGANIZATION", R.drawable.ic_chinh_phu);

    companion object {
        private val mapType = values().associateBy(TYPE_WORKSPACES::type)
        fun fromIsTypeWorkSpaces(type: String?) = mapType[type]
    }
}
