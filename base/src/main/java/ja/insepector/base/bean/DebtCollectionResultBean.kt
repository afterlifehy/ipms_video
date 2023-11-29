package ja.insepector.base.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class DebtCollectionResultBean(
    val result: List<DebtCollectionBean>
)

@Parcelize
data class DebtCollectionBean(
    val companyName: String,
    val companyPhone: String,
    val districtId: String,
    val dueMoney: Int,
    val endTime: String,
    val orderNo: String,
    val orderType: Int,
    val oweMoney: Int,
    val oweOrderId: String,
    val paidMoney: Int,
    val parkingNo: String,
    val parkingTime: String,
    val startTime: String,
    val streetName: String,
    val streetNo: String,
    val carLicense: String
) : Parcelable {

}