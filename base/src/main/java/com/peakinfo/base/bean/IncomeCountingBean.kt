package com.peakinfo.base.bean

data class IncomeCountingBean(
    val payMoneyToday: String = "",
    val orderTotalToday: Int = 0,
    val unclearedTotal: Int = 0,
    val payMoneyTotal: String = "",
    val orderTotal: Int = 0,
    var loginName: String = "",
    var range: String = "",
    var list1: ArrayList<Summary> = arrayListOf(),
    var list2: ArrayList<Summary> = arrayListOf(),
)

data class Summary(var number: Int, var amount: String, var streetName: String,var streetNo:String)