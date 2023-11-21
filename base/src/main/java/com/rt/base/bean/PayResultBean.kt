package com.rt.base.bean

data class PayResultResultBean(
    val result: PayResultBean
)

data class PayResultBean(
    val description: String,
    val payMoney: Int,
    val payStatus: Int,
    val payTime: Long,
    val payType: Int,
    val tradeNo: String
)