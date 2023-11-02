package com.rt.base.base.mvvm

import com.rt.base.BuildConfig

object UrlManager {
    //Todo url
    const val DEV_HOST = "http://13.229.90.206:8080/"
    const val FORMAL_HOST = "https://api.runba.xyz/"

    const val WEB3J_HTTP_URL = "https://data-seed-prebsc-1-s1.binance.org:8545"
    const val FORMAL_WEB3J_HTTP_URL = "https://bsc-dataseed1.binance.org/"
//    const val FORMAL_WEB3J_HTTP_URL = "https://bsc-dataseed1.ninicoin.io/"

    fun getServerUrl(): String {
        if (BuildConfig.is_dev) {
            return DEV_HOST
        } else {
            return FORMAL_HOST
        }
    }

    fun getWeb3jHttpUrl(): String {
        if (BuildConfig.is_dev) {
            return WEB3J_HTTP_URL
        } else {
            return FORMAL_WEB3J_HTTP_URL
        }
    }
}