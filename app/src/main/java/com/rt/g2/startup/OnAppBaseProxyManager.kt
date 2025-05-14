package com.rt.g2.startup

import com.rt.base.proxy.OnAppBaseProxyLinsener
import com.rt.g2.BuildConfig

class OnAppBaseProxyManager : OnAppBaseProxyLinsener {
    override fun onIsProxy(): Boolean {
        return BuildConfig.is_proxy
    }

    override fun onIsDebug(): Boolean {
        return BuildConfig.is_debug
    }

}