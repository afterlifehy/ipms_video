package com.peakinfo.base.bean.ca

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OwemoneyInfoBean(
    val orderId: String?,
    val orderType: Int?,
    val businessId: String?,
    val districtId: String?,
    val roadId: String?,
    val roadName: String?,
    val companyName: String?,
    val companyPhone: String?,
    val berthId: String?,
    val arrivedTime: Long?,
    val leftTime: Long?,
    val parkingTime: Long?,
    val dueMoney: Long? ,
    val paidMoney: Long? ,
    val oweMoney: Long?,
    var carLicense:String?,
): Parcelable