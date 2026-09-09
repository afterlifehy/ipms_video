package com.peakinfo.base.bean.ca

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UrgeBean(
    val urgePayId: String,
    val subject:String,
    val oweCount: Int,
    val oweAmount:Int,
    val oweBeginTime:Long,
    val oweEndTime:Long,
    val urgePayType: Int,
    val plateId: String,
    val plateColor: Int,
    val urgeCount:Int,
    val districtId: String,
    val urgePayFile: String,
    val overdueTime:Long,
    val dataTime: Long
): Parcelable