package com.rt.base.bean

data class TransactionResultBean(
    val result: List<TransactionBean>
)

data class TransactionBean(
    val carLicense: String,
    val hasPayed: String,
    val orderNo: String,
    val oweMoney: String,
    val parkingNo: String,
    val payedAmount: String,
    val tradeNo: String,
    val startDate:String,
    val endDate:String
)