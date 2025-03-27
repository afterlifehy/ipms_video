package com.peakinfo.base.bean

data class PayResultBean(
    var orderId: String,
    var businessCname: String,
    var carLicense: String,
    var description: String,
    var endTime: String,
    var oweCount: Int,
    var payMoney: String,
    var payStatus: Int,
    var payTime: Long,
    var payType: Int,
    var phone: String,
    var remark: String,
    var roadName: String,
    var startTime: String,
    var tradeNo: String
)

data class NoticePrintResultBean(
    var result: ArrayList<PayResultBean>
)