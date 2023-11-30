package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.common.realm.RealmUtil
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