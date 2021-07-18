package vn.ncsc.visafe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import vn.ncsc.visafe.model.response.StatsWorkspaceResponse
import vn.ncsc.visafe.model.response.TopCategories

@Parcelize
data class StatsWorkSpace(
    @SerializedName("time_units")
    var time_units: String? = null,
    @SerializedName("num_dns_queries")
    var num_dns_queries: Int? = null,
    @SerializedName("num_blocked_filtering")
    var num_blocked_filtering: Int? = null,
    @SerializedName("num_replaced_safebrowsing")
    var num_replaced_safebrowsing: Int? = null,
    @SerializedName("num_replaced_safesearch")
    var num_replaced_safesearch: Int? = null,
    @SerializedName("num_replaced_parental")
    var num_replaced_parental: Int? = null,
    @SerializedName("num_dangerous_domain")
    var num_dangerous_domain: Int? = null,
    @SerializedName("num_violation")
    var num_violation: Int? = null,
    @SerializedName("num_ads_blocked")
    var num_ads_blocked: Int? = null,
    @SerializedName("avg_processing_time")
    var avg_processing_time: Int? = null,
    @SerializedName("top_queried_domains")
    var top_queried_domains: List<String>? = listOf(),
    @SerializedName("top_clients")
    var top_clients: List<String>? = listOf(),
    @SerializedName("top_blocked_domains")
    var top_blocked_domains: List<String>? = listOf(),
    @SerializedName("dns_queries")
    var dns_queries: List<String>? = listOf(),
    @SerializedName("blocked_filtering")
    var blocked_filtering: List<String>? = listOf(),
    @SerializedName("replaced_safebrowsing")
    var replaced_safebrowsing: List<String>? = listOf(),
    @SerializedName("replaced_parental")
    var replaced_parental: List<String>? = listOf(),
    @SerializedName("top_categories")
    var top_categories: List<TopCategories>? = listOf()
) : Parcelable {
    constructor(statsWorkspaceResponse: StatsWorkspaceResponse) : this(
        time_units = statsWorkspaceResponse.time_units,
        num_dns_queries = statsWorkspaceResponse.num_dns_queries,
        num_blocked_filtering = statsWorkspaceResponse.num_blocked_filtering,
        num_replaced_safebrowsing = statsWorkspaceResponse.num_replaced_safebrowsing,
        num_replaced_safesearch = statsWorkspaceResponse.num_replaced_safesearch,
        num_replaced_parental = statsWorkspaceResponse.num_replaced_parental,
        num_dangerous_domain = statsWorkspaceResponse.num_dangerous_domain,
        num_violation = statsWorkspaceResponse.num_violation,
        num_ads_blocked = statsWorkspaceResponse.num_ads_blocked,
        avg_processing_time = statsWorkspaceResponse.avg_processing_time,
        top_queried_domains = statsWorkspaceResponse.top_queried_domains,
        top_clients = statsWorkspaceResponse.top_clients,
        top_blocked_domains = statsWorkspaceResponse.top_blocked_domains,
        dns_queries = statsWorkspaceResponse.dns_queries,
        blocked_filtering = statsWorkspaceResponse.blocked_filtering,
        replaced_safebrowsing = statsWorkspaceResponse.replaced_safebrowsing,
        replaced_parental = statsWorkspaceResponse.replaced_parental,
        top_categories = statsWorkspaceResponse.top_categories
    )
}

