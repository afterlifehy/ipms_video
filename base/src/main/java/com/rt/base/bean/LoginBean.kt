package com.rt.base.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class LoginBean(
    val name: String,
    val phone: String,
    val result: List<Street>,
    val token: String
)

@Parcelize
data class Street(
    val streetName: String,
    val streetNo: String
) : Parcelable