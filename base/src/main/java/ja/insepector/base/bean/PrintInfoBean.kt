package ja.insepector.base.bean

class PrintInfoBean(
    val roadId: String,
    val plateId: String,
    val payMoney: String,
    val orderId: String,
    val phone: String,
    val startTime: String,
    val leftTime: String,
    val remark: String,
    val company: String,
    val oweCount: Int
) {
    override fun toString(): String = "$orderId,$plateId,$roadId,$startTime,$leftTime,$payMoney,$oweCount,$phone,$remark,$company"
}