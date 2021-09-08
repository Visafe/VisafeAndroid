package vn.ncsc.visafe.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
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
    @SerializedName("IsActive")
    var isActive: Boolean? = null,
    @SerializedName("DefaultGroup")
    var DefaultGroup: String? = null,
    @SerializedName("DefaultWorkspace")
    var DefaultWorkspace: String? = null,
    @SerializedName("AccountType")
    var AccountType: String? = null,
    @SerializedName("TimeStart")
    var TimeStart: String? = null,
    @SerializedName("TimeEnd")
    var TimeEnd: String? = null,
    @SerializedName("MaxWorkspace")
    var MaxWorkspace: Int? = null,
    @SerializedName("MaxGroup")
    var MaxGroup: Int? = null,
    @SerializedName("MaxDevice")
    var MaxDevice: Int? = null
)