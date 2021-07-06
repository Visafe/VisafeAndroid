package com.vn.visafe_android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupData(
    val name: String? = null,
    val amoutMember: Int? = null,
    val amoutDevice: Int? = null,
    val image: String? = null
) : Parcelable