package com.peakinfo.base.base.mvvm

import com.peakinfo.base.BuildConfig

object UrlManager {
    //    const val DEV_HOST = "http://10.0.0.73:8080/ipms/service/"
    const val RT_DEV_HOST = "http://114.94.20.110/ipms/service/"
    const val RT_FORMAL_HOST = "http://ipms.rentongtek.com/ipms/service/"

    const val DEV_HOST = "http://180.166.5.198:28080/"
    const val FORMAL_HOST = "http://roadparking.jtcx.sh.cn/"

//    const val DEV_CA = "https://device.ysq.mkeysec.net/sdk/v1"
    const val DEV_CA = "http://103.36.136.173:1880/posadmin"
    const val FORMAL_CA = "https://parkapp.jtcx.sh.cn/ca/possdk/v1"

    fun getRTServerUrl(): String {
        if (BuildConfig.is_dev) {
            return RT_DEV_HOST
        } else {
            return RT_FORMAL_HOST
        }
    }

    fun getServerUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_HOST
        } else {
            return FORMAL_HOST
        }
    }

    fun getCAUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_CA
        } else {
            return FORMAL_CA
        }
    }
}
