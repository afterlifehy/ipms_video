package com.peakinfo.base.bean

data class PayResultBean(
    val businessCname: String,
    val carLicense: String,
    val description: String,
    val endTime: String,
    val oweCount: Int,
    val payMoney: String,
    val payStatus: Int,
    val payTime: Long,
    val payType: Int,
    val phone: String,
    val remark: String,
    val roadName: String,
    val startTime: String,
    val tradeNo: String
)