package com.rt.ipms_video.startup

import android.app.Application
import android.net.http.HttpResponseCache
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.network.NetWorkMonitorManager
import com.rt.base.start.AppInitManager
import com.rt.common.help.SmartRefreshHelp
import java.io.File

class AppStartUpManager private constructor() : AppInitManager() {
    private val TAG: String = AppStartUpManager::class.java.simpleName

    companion object {
        var mAppStartUpManager: AppStartUpManager? = null
        fun instance(): AppStartUpManager {
            if (mAppStartUpManager == null) {
                mAppStartUpManager = AppStartUpManager()
            }
            return mAppStartUpManager!!
        }
    }

    override fun applicationInit(application: Application) {
//        CrashHandler.instance()?.initCrash(application)
        val cacheDir = File(BaseApplication.instance().cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)

        if (com.rt.base.BuildConfig.is_debug) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(BaseApplication.instance())

        //初始化网络状态监听
        regNewWorkState(application)
    }

    override fun delayInit(application: Application) {
        BaseApplication.instance().setOnAppBaseProxyLinsener(OnAppBaseProxyManager())        //初始化全局的刷新
        SmartRefreshHelp.initRefHead()
    }

    /**
     * 注册全局的网络状态广播
     */
    private fun regNewWorkState(application: Application) {
        NetWorkMonitorManager.getInstance().init(application)
    }

}