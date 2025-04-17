package com.rt.base.bean.ca

data class OweMoneyBean(
    val amount: Int,
    val channel: String,
    val orderId: String,
    val orderType: Int,
    val oweOrderId: String,
    val ttl: Int,
    val businessId:String,
)