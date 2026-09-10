package com.peakinfo.base.bean.ca

data class UrgeOrderBean(
    val arrivedTime: Long,
    val businessId: String,
    val leftTime: Long,
    val orderId: String,
    val oweMoney: Int,
    val paidMoney: Int,
    val payTime: Long,
    val roadId: String,
    val roadName: String
)