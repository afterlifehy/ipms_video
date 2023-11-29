package ja.insepector.base.bean

data class IncomeCountingBean(
    val payMoneyToday: String,
    val orderTotalToday: Int,
    val unclearedTotal: Int,
    val payMoneyTotal: String,
    val orderTotal: Int
)