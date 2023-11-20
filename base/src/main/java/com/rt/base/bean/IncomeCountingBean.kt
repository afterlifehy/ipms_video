package com.rt.base.bean

data class IncomeCountingBean(
    val orderTotal: Int,
    val orderTotalToday: Int,
    val payMoneyToday: Double,
    val payMoneyTotal: Double,
    val personalTotal: Double
)