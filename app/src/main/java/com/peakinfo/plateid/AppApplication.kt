package com.peakinfo.plateid

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.http.HttpResponseCache
import com.alibaba.android.arouter.launcher.ARouter
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.R
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.dialog.DialogHelp
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.event.ReLoginEvent
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.http.interceptor.*
import com.peakinfo.base.network.NetWorkMonitorManager
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.common.event.CurrentStreetUpdateEvent
import com.peakinfo.common.help.SmartRefreshHelp
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.plateid.startup.OnAppBaseProxyManager
import com.peakinfo.plateid.ui.activity.login.LoginActivity
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
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.loginName, "")
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
        UMConfigure.init(this, "657c1359a7208a5af1884f6c", "android", UMConfigure.DEVICE_TYPE_PHONE, null)
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