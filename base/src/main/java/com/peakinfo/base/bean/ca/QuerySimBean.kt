package com.peakinfo.base.bean.ca

import android.os.Parcelable
import com.peakinfo.base.bean.Street
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuerySimBean(
    val result: List<Street>?,
    val code: String = "",
    val certSn: String?,
    val state: String?,
    var appIdLast: String = "",
    var unitName :String = "",
    var pin:String = "",
    var unitId:String = ""
) : Parcelable
