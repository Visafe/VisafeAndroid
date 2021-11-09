package vn.ncsc.visafe.model.response

import com.google.gson.annotations.SerializedName
import vn.ncsc.visafe.model.QueryLogData

class QueryLogResponse {
    @SerializedName("data")
    var data: MutableList<QueryLogData>? = null
}