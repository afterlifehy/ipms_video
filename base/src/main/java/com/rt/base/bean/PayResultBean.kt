package com.rt.base.bean

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
    var tradeNo: String,
    var qrcode:String,
    var orderType:Int = 1
)

data class NoticePrintResultBean(
    var result: ArrayList<PayResultBean>
)