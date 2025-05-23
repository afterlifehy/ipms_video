package com.peakinfo.base.bean

data class FeeRateResultBean(
    val result: List<FeeRateBean>
)

data class FeeRateBean(
    val blackEnd: String,
    val blackPeriod: String,
    val blackStart: String,
    val dateType: Int,
    val first: String,
    val second: String,
    val third: String,
    val unitPrice: String,
    val whiteEnd: String,
    val whiteStart: String,
    val period: String
)