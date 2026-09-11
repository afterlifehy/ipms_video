package com.peakinfo.base.bean.ca

data class UrgeDetailBean(
    val monthList: List<UrgeMonthBean>,
    val oweList: List<UrgeOrderBean>
)
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