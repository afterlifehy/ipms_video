package com.peakinfo.base.bean

data class TransactionResultBean(
    var result: List<TransactionBean>
)

data class TransactionBean(
    var carLicense: String,
    var endTime: String,
    var hasPayed: String,
    var orderNo: String,
    var oweMoney: String,
    var parkingNo: String,
    var payedAmount: String,
    var startTime: String,
    var tradeNo: String,
    var orderType: String = "2"
)