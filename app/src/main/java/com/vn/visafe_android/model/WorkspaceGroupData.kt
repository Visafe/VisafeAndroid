package com.vn.visafe_android.model

data class WorkspaceGroupData(
    val name: String? = null,
    val amoutGroupChild: Int? = null,
    val status: String? = null,
    var isSelected: Boolean? = null
)

enum class StatusGroup private constructor(val status : String) {
    QUAN_TRI("Quản trị"),
    THANH_VIEN("Thành viên")
}
