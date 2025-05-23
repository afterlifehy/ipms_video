package com.peakinfo.plateid.startup

import com.peakinfo.base.proxy.OnAppBaseProxyLinsener
import com.peakinfo.plateid.BuildConfig

class OnAppBaseProxyManager : OnAppBaseProxyLinsener {
    override fun onIsProxy(): Boolean {
        return BuildConfig.is_proxy
    }

    override fun onIsDebug(): Boolean {
        return BuildConfig.is_debug
    }

}