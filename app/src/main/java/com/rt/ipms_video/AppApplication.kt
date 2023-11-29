package com.rt.ipms_video

import android.app.Application
import android.content.Context
import android.net.http.HttpResponseCache
import com.rt.base.BaseApplication
import com.rt.base.http.interceptor.*
import com.rt.common.help.SmartRefreshHelp
import com.rt.ipms_video.startup.OnAppBaseProxyManager
import io.realm.Realm
import okhttp3.Interceptor
import java.io.File

class AppApplication : BaseApplication() {
    companion object {
        var _context: BaseApplication? = null
        fun instance(): BaseApplication {
            return com.rt.ipms_video.AppApplication.Companion._context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        com.rt.ipms_video.AppApplication.Companion._context = this
        //realm
        Thread {
            Realm.init(this)
            val cacheDir = File(BaseApplication.instance().cacheDir, "http")
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
            BaseApplication.instance().setOnAppBaseProxyLinsener(OnAppBaseProxyManager())        //初始化全局的刷新
            SmartRefreshHelp.initRefHead()
            //初始化网络状态监听
            regNetWorkState(this)
        }.start()
    }


    /**
     * 注册全局的网络状态广播
     */
    private fun regNetWorkState(application: Application) {
        com.rt.base.network.NetWorkMonitorManager.getInstance().init(application)
    }

    override fun onAddOkHttpInterceptor(): List<Interceptor> {
        val list = ArrayList<Interceptor>()
        list.add(HeaderInterceptor())
        list.add(LoginExpiredInterceptor())
        list.add(com.rt.base.http.interceptor.HostInterceptor())
        list.add(TokenInterceptor())
        if (com.rt.ipms_video.BuildConfig.is_debug) {
            list.add(com.rt.base.http.interceptor.LogInterceptor(com.rt.ipms_video.BuildConfig.is_debug))
            val mHttpLoggingInterceptor = com.rt.base.http.interceptor.HttpLoggingInterceptor("rt_http")
            mHttpLoggingInterceptor.setPrintLevel(com.rt.base.http.interceptor.HttpLoggingInterceptor.Level.BODY)
            list.add(mHttpLoggingInterceptor)
        }
        return list
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

}