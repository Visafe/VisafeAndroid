package vn.ncsc.visafe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.model.response.TopCategories

@Parcelize
data class StatsWorkSpace(
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
) : Parcelable {
    constructor(statsWorkspaceResponse: StatsWorkspaceResponse) : this(
        time_units = statsWorkspaceResponse.time_units,
        num_dns_queries = statsWorkspaceResponse.num_dns_queries,
        num_dangerous_domain = statsWorkspaceResponse.num_dangerous_domain,
        num_violation = statsWorkspaceResponse.num_violation,
        num_ads_blocked = statsWorkspaceResponse.num_ads_blocked,
        num_native_tracking = statsWorkspaceResponse.num_native_tracking,
        num_access_blocked = statsWorkspaceResponse.num_access_blocked,
        num_content_blocked = statsWorkspaceResponse.num_content_blocked,
        num_dangerous_domain_all = statsWorkspaceResponse.num_dangerous_domain_all,
        num_violation_all = statsWorkspaceResponse.num_violation_all,
        num_ads_blocked_all = statsWorkspaceResponse.num_ads_blocked_all,
        num_native_tracking_all = statsWorkspaceResponse.num_native_tracking_all,
        num_access_blocked_all = statsWorkspaceResponse.num_access_blocked_all,
        num_content_blocked_all = statsWorkspaceResponse.num_content_blocked_all,
        top_categories = statsWorkspaceResponse.top_categories
    )
}

