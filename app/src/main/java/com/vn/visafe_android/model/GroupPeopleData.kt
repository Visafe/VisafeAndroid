package com.vn.visafe_android.model

data class GroupPeopleData(
    val name: String? = null,

    val amoutDanger: Int? = null,
    val amoutInfringe: Int? = null,
    val amoutAdvertisement: Int? = null,
    val amoutAccess: Int? = null,

    val groupPeopleNotiDataList: List<GroupPeopleNotiData>? = null
)

data class GroupPeopleNotiData(
    val title: String? = null,
    val contentWeb: String? = null
)