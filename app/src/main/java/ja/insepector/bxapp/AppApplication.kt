package ja.insepector.bxapp

import android.app.Application
import android.content.Context
import android.net.http.HttpResponseCache
import ja.insepector.base.BaseApplication
import ja.insepector.base.http.interceptor.*
import ja.insepector.common.help.SmartRefreshHelp
import ja.insepector.bxapp.startup.OnAppBaseProxyManager
import io.realm.Realm
import okhttp3.Interceptor
import java.io.File

class AppApplication : BaseApplication() {
    companion object {
        var _context: BaseApplication? = null
        fun instance(): BaseApplication {
            return ja.insepector.bxapp.AppApplication.Companion._context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        ja.insepector.bxapp.AppApplication.Companion._context = this
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
        ja.insepector.base.network.NetWorkMonitorManager.getInstance().init(application)
    }

    override fun onAddOkHttpInterceptor(): List<Interceptor> {
        val list = ArrayList<Interceptor>()
        list.add(HeaderInterceptor())
        list.add(LoginExpiredInterceptor())
        list.add(ja.insepector.base.http.interceptor.HostInterceptor())
        list.add(TokenInterceptor())
        if (ja.insepector.bxapp.BuildConfig.is_debug) {
            list.add(ja.insepector.base.http.interceptor.LogInterceptor(ja.insepector.bxapp.BuildConfig.is_debug))
            val mHttpLoggingInterceptor = ja.insepector.base.http.interceptor.HttpLoggingInterceptor("rt_http")
            mHttpLoggingInterceptor.setPrintLevel(ja.insepector.base.http.interceptor.HttpLoggingInterceptor.Level.BODY)
            list.add(mHttpLoggingInterceptor)
        }
        return list
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

}