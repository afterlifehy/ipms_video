package ja.insepector.bxapp.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import ja.insepector.base.BaseApplication
import ja.insepector.base.base.mvvm.BaseViewModel
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.common.realm.RealmUtil
import kotlinx.coroutines.runBlocking

open class BaseInfoViewModel : BaseViewModel() {
    val baseInfoLiveData = MutableLiveData<MutableList<String>>()

    fun getBaseInfo() {
        val baseInfoList: MutableList<String> = ArrayList()
        val street = RealmUtil.instance?.findCurrentStreet()
        return runBlocking {
            val name = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.name)
            val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
            val phone = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.phone)

            baseInfoList.add(name)
            baseInfoList.add(loginName)
            baseInfoList.add(phone)
            baseInfoList.add(street!!.streetName)
            baseInfoLiveData.value = baseInfoList
        }
    }
}