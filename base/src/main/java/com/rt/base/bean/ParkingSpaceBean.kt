package com.rt.base.bean

data class ParkingSpaceBean(
    val amountPayed: Int,
    val amountPending: Int,
    val amountTotal: Int,
    val carColor: Int,
    val carLicense: String,
    val orderNo: String,
    val parkingNo: String,
    val parkingTime: Int,
    val startTime: String,
    val streetName: String,
    val streetNo: String,
    val tradeNo: String
)