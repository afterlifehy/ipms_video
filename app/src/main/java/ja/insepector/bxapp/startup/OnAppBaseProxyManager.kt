package ja.insepector.bxapp.startup

import ja.insepector.base.proxy.OnAppBaseProxyLinsener

class OnAppBaseProxyManager : OnAppBaseProxyLinsener {
    override fun onIsProxy(): Boolean {
        return ja.insepector.bxapp.BuildConfig.is_proxy
    }

    override fun onIsDebug(): Boolean {
        return ja.insepector.bxapp.BuildConfig.is_debug
    }

}