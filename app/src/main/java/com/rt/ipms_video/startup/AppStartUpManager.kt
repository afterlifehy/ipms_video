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
    }

    override fun delayInit(application: Application) {

    }
}