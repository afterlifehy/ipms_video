package com.peakinfo.base.base.mvvm

import com.peakinfo.base.BuildConfig

object UrlManager {
    //    const val DEV_HOST = "http://10.0.0.73:8080/ipms/service/"
    const val DEV_HOST = "http://114.94.20.110/ipms/service/"
    const val FORMAL_HOST = "http://ipms.csnits.com/ipms/service/"
//    const val DEV_HOST = "http://csnits.com/JingAnManagerApp/service/"

    fun getServerUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_HOST
        } else {
            return FORMAL_HOST
        }
    }
}
