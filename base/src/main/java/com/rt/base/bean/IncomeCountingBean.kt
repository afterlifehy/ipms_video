package com.rt.base.bean

data class IncomeCountingBean(
    val payMoneyToday: Double,
    val orderTotalToday: Int,
    val unclearedTotal: Int,
    val payMoneyTotal: Double,
    val orderTotal: Int
)