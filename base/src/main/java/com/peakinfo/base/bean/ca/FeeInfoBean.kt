package com.peakinfo.base.bean.ca

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeeInfoBean(
    val businessId: String?,
    val orderId: String?,
    val berthId: String?,
    val roadId: String?,
    val roadName: String?,
    val plateId: String?,
    val monthPay: Long? = 0L,
    val plateColor: Int?,
    val arrivedTime: Long?,
    val leftTime: Long?,
    val parkingTime: Long?,
    val left: Boolean?,
    val dueMoney: Long?,
    val prepayMoney: Long?,
    val payMoney: Long?,
    val expiredTime: Long?
) : Parcelable
