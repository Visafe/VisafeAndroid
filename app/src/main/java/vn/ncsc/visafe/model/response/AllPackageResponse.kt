package vn.ncsc.visafe.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllPackageResponse(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("max_workspace")
    var max_workspace: Int? = null,
    @SerializedName("max_group")
    var max_group: Int? = null,
    @SerializedName("max_device")
    var max_device: Int? = null,
    @SerializedName("prices")
    var prices: MutableList<PriceAllPackage>? = null
) : Parcelable

@Parcelize
data class PriceAllPackage(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("duration")
    var duration: Int? = null,
    @SerializedName("day_trail")
    var day_trail: Int? = null,
    @SerializedName("price")
    var price: Long? = null
) : Parcelable