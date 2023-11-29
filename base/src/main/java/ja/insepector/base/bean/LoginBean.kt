package ja.insepector.base.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginBean(
    val name: String,
    val loginName: String,
    val phone: String,
    val result: List<Street>,
    val token: String
):Parcelable