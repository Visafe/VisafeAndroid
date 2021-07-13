package vn.ncsc.visafe.data

data class BaseResponseHasResults<T>(
    var count: String = "", var next: String = "",
    var previous: String = "", var results: T
)