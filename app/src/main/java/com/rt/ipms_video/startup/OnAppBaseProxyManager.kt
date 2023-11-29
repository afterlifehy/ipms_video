package com.rt.ipms_video.startup

import com.rt.base.proxy.OnAppBaseProxyLinsener

class OnAppBaseProxyManager : OnAppBaseProxyLinsener {
    override fun onIsProxy(): Boolean {
        return com.rt.ipms_video.BuildConfig.is_proxy
    }

    override fun onIsDebug(): Boolean {
        return com.rt.ipms_video.BuildConfig.is_debug
    }

}