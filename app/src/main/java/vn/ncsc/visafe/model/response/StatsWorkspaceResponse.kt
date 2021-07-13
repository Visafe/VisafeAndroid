package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName

data class StatsWorkspaceResponse(
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
)

data class TopCategories(
    @SerializedName("apps")
    var apps: List<AppsData>? = listOf(),
    @SerializedName("count")
    var count: Float? = null,
    @SerializedName("name")
    var name: String? = null
)

data class AppsData(
    @SerializedName("name")
    var apps: String? = null,
    @SerializedName("count")
    var count: Float? = null
)