package com.rt.ipms_video.startup

import android.app.Application
import com.rt.base.BaseApplication
import com.rt.base.network.NetWorkMonitorManager
import com.rt.base.start.AppInitManager
import com.rt.common.help.SmartRefreshHelp

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
        BaseApplication.instance().setOnAppBaseProxyLinsener(OnAppBaseProxyManager())        //初始化全局的刷新
        SmartRefreshHelp.initRefHead()
        //初始化网络状态监听
        regNewWorkState(application)
    }

    override fun delayInit(application: Application) {

    }

    /**
     * 注册全局的网络状态广播
     */
    private fun regNewWorkState(application: Application) {
        NetWorkMonitorManager.getInstance().init(application)
    }

}