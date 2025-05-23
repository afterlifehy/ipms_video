package com.peakinfo.base.bean

data class ParkingLotResultBean(
    val result: List<ParkingLotBean>
)

data class ParkingLotBean(
    var carColor: String,
    val carLicense: String,
    val cleared: String,
    val orderNo: String,
    val parkingNo: String,
    var state: String,
    val deadLine: Long,
    val monthPay: Long
)