package vn.ncsc.visafe.model

import com.google.gson.annotations.SerializedName

data class QueryLogData(
    @SerializedName("client")
    var client: String? = null,
    @SerializedName("client_id")
    var client_id: String? = null,
    @SerializedName("client_proto")
    var client_proto: String? = null,
    @SerializedName("doc_id")
    var doc_id: String? = null,
    @SerializedName("elapsedMs")
    var elapsedMs: String? = null,
    @SerializedName("filterId")
    var filterId: Int? = null,
    @SerializedName("group_id")
    var group_id: String? = null,
    @SerializedName("question")
    var question: QuestionQuery? = null,
    @SerializedName("reason")
    var reason: String? = null,
    @SerializedName("rule")
    var rule: String? = null,
    @SerializedName("rules")
    var rules: MutableList<RulesQuery>? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("upstream")
    var upstream: String? = null
)

data class QuestionQuery(
    @SerializedName("class")
    var classQuestion: String? = null,
    @SerializedName("host")
    var host: String? = null,
    @SerializedName("type")
    var type: String? = null
)

data class RulesQuery(
    @SerializedName("filter_list_id")
    var filter_list_id: Int? = null,
    @SerializedName("text")
    var text: String? = null
)
