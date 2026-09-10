package com.peakinfo.base.bean.ca

data class UrgeMonthBean(
    val applyId: String,
    val companyId: String,
    val companyName: String,
    val districtId: String,
    val groupId: String,
    val month: String,
    val monthPayId: String,
    val overdueTime: Long,
    val oweAmount: Int,
    val oweCount: Int,
    val paidMoney: Int,
    val payMoney: Int,
    val status: Int
)