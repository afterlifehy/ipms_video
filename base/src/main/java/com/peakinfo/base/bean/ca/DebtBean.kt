package com.peakinfo.base.bean.ca

data class DebtBean(
    val arrivedTime: Long,
    val berthId: String,
    val businessId: String,
    val companyName: String,
    val companyPhone: String,
    val districtId: String,
    val dueMoney: Int,
    val leftTime: Long,
    val orderId: String,
    val orderType: Int,
    val oweMoney: Int,
    val paidMoney: Int,
    val parkingTime: Int,
    val photos: Photos,
    val roadId: String,
    val roadName: String
)

data class Photos(
    val arrivePlate: String,
    val arriveVehicle: String,
    val leavePlate: String,
    val leaveVehicle: String
)