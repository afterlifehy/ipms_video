package com.rt.g2

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.http.HttpResponseCache
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.R
import com.rt.base.arouter.ARouterMap
import com.rt.base.dialog.DialogHelp
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.event.ReLoginEvent
import com.rt.base.ext.i18n
import com.rt.base.help.ActivityCacheManager
import com.rt.base.http.interceptor.*
import com.rt.base.network.NetWorkMonitorManager
import com.rt.common.help.SmartRefreshHelp
import com.rt.common.realm.RealmUtil
import com.rt.g2.startup.OnAppBaseProxyManager
import com.rt.g2.ui.activity.login.LoginActivity
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.realm.Realm
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class AppApplication : BaseApplication() {
    companion object {
        var _context: BaseApplication? = null
        fun instance(): BaseApplication {
            return _context!!
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(reLoginEvent: ReLoginEvent) {
        DialogHelp.Builder().setTitle(i18n(R.string.登录信息异常请重新签到))
            .setLeftMsg(i18n(R.string.取消))
            .setRightMsg(i18n(R.string.去签到)).setCancelable(true)
            .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                override fun onLeftClickLinsener(msg: String) {
                }

                override fun onRightClickLinsener(msg: String) {
                    ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                    for (i in ActivityCacheManager.instance().getAllActivity()) {
                        if (i !is LoginActivity) {
                            i.finish()
                        }
                    }
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, "")
                    }
                    RealmUtil.instance?.deleteAllStreet()
                }

            }).build(ActivityCacheManager.instance().getCurrentActivity()).showDailog()
    }

    override fun onCreate() {
        super.onCreate()
        _context = this
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
        UMConfigure.init(this, "6824345fbc47b67d8365936a", "android", UMConfigure.DEVICE_TYPE_PHONE, null)
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL)
        UMConfigure.setProcessEvent(true)

        EventBus.getDefault().register(this)
    }


    /**
     * 注册全局的网络状态广播
     */
    private fun regNetWorkState(application: Application) {
        NetWorkMonitorManager.getInstance().init(application)
    }

    override fun onAddOkHttpInterceptor(): List<Interceptor> {
        val list = ArrayList<Interceptor>()
        list.add(HeaderInterceptor())
        list.add(LoginExpiredInterceptor())
        list.add(HostInterceptor())
        list.add(TokenInterceptor())
        list.add(CAInterceptor())
        if (BuildConfig.is_debug) {
            list.add(LogInterceptor(BuildConfig.is_debug))
            val mHttpLoggingInterceptor = HttpLoggingInterceptor("rt_http")
            mHttpLoggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
            list.add(mHttpLoggingInterceptor)
        }
        return list
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onTerminate() {
        super.onTerminate()
        EventBus.getDefault().unregister(this)
    }
}