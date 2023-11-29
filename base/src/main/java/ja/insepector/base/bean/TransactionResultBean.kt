package ja.insepector.base.bean

data class TransactionResultBean(
    val result: List<TransactionBean>
)

data class TransactionBean(
    val carLicense: String,
    val endTime: String,
    var hasPayed: String,
    val orderNo: String,
    val oweMoney: String,
    val parkingNo: String,
    var payedAmount: String,
    val startTime: String,
    val tradeNo: String,
)