package com.vn.visafe_android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subject(
    var title: String,
    val icon: Int,
    var isChecked: Boolean = false
) : Parcelable