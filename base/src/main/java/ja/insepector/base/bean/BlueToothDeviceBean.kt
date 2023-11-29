package ja.insepector.base.bean

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BlueToothDeviceBean(
    @PrimaryKey
    var address: String = "",
    var name: String = ""
) : RealmObject() {
}
