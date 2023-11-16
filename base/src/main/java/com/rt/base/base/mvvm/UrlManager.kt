package com.rt.base.base.mvvm

import com.rt.base.BuildConfig

object UrlManager {
    //Todo url
    const val DEV_HOST = "http://180.169.37.244/ipms/service/"
    const val FORMAL_HOST = "https://api.runba.xyz/"

    fun getServerUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_HOST
        } else {
            return FORMAL_HOST
        }
    }
}