package com.rt.base.bean.ca

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QRInfoBean(
    val qrCode: String?,
    val orderId: String?,
    val payUrl: String?,
    val ttl: Int?,
    val payMoney: Int?,
    val qrcode:String?
) : Parcelable