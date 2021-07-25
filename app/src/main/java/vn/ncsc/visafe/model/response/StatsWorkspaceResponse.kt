package vn.ncsc.visafe.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class StatsWorkspaceResponse(
    @SerializedName("time_units")
    var time_units: String? = "",
    @SerializedName("num_dns_queries")
    var num_dns_queries: Int? = 0,
    @SerializedName("num_dangerous_domain")
    var num_dangerous_domain: Int? = 0,
    @SerializedName("num_violation")
    var num_violation: Int? = 0,
    @SerializedName("num_ads_blocked")
    var num_ads_blocked: Int? = 0,
    @SerializedName("num_native_tracking")
    var num_native_tracking: Int? = 0,
    @SerializedName("num_access_blocked")
    var num_access_blocked: Int? = 0,
    @SerializedName("num_content_blocked")
    var num_content_blocked: Int? = 0,
    @SerializedName("num_dangerous_domain_all")
    var num_dangerous_domain_all: Int? = 0,
    @SerializedName("num_violation_all")
    var num_violation_all: Int? = 0,
    @SerializedName("num_ads_blocked_all")
    var num_ads_blocked_all: Int? = 0,
    @SerializedName("num_native_tracking_all")
    var num_native_tracking_all: Int? = 0,
    @SerializedName("num_access_blocked_all")
    var num_access_blocked_all: Int? = 0,
    @SerializedName("num_content_blocked_all")
    var num_content_blocked_all: Int? = 0,
    @SerializedName("top_categories")
    var top_categories: List<TopCategories>? = listOf()
)

@Parcelize
data class TopCategories(
    @SerializedName("apps")
    var apps: List<AppsData>? = listOf(),
    @SerializedName("count")
    var count: Float? = null,
    @SerializedName("name")
    var name: String? = null
) : Parcelable

@Parcelize
data class AppsData(
    @SerializedName("name")
    var apps: String? = null,
    @SerializedName("count")
    var count: Float? = null
) : Parcelable