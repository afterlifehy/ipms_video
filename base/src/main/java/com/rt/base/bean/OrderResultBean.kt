package com.rt.base.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class OrderResultBean(
    val result: List<OrderBean>
)

@Parcelize
data class OrderBean(
    val amount: String,
    val carLicense: String,
    var duration: String,
    var duration2: String,
    val endTime: String,
    val hasPayed: String,
    val orderNo: String,
    val parkingNo: String,
    val startTime: String,
    val streetName: String,
    val paidAmount: String
) : Parcelable {
    init {
        if (duration == null) {
            duration = "0"
        }
        if (duration2.isNullOrEmpty()) {
            duration2 = "0"
        }
    }
}