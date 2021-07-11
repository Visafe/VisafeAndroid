package com.vn.visafe_android.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeviceGroup(
    @SerializedName("groupID")
    var groupID: String? = null,
    @SerializedName("deviceID")
    var deviceID: String? = null,
    @SerializedName("deviceName")
    var deviceName: String? = null,
    @SerializedName("DeviceType")
    var DeviceType: String? = null,
    @SerializedName("deviceOwner")
    var deviceOwner: String? = null,
    @SerializedName("deviceJoinState")
    var deviceJoinState: String? = null,
    @SerializedName("DeviceDetail")
    var DeviceDetail: String? = null,
    @SerializedName("deviceActiveState")
    var deviceActiveState: String? = null,
    @SerializedName("deviceMonitorID")
    var deviceMonitorID: Int? = null,
    @SerializedName("identifierId")
    var identifierId: String? = null,
    @SerializedName("identifierName")
    var identifierName: String? = null
) : Parcelable

@Parcelize
data class DeviceDetail(
    @SerializedName("androidVersion")
    var androidVersion: String? = null,
    @SerializedName("kernel")
    var kernel: String? = null
) : Parcelable