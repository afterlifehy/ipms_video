package com.peakinfo.base.bean

data class NotificationBean(
    val businessCname: String,
    val carLicense: String,
    val endTime: String,
    val payMoney: String,
    val phone: String,
    val remark: String,
    val roadName: String,
    val startTime: String,
    val tradeNo: String,
    val oweCount: Int
)