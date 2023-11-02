package com.rt.ipms_video

import android.content.Context
import android.net.http.HttpResponseCache
import com.alibaba.android.arouter.launcher.ARouter
import com.rt.base.BaseApplication
import com.rt.base.http.interceptor.*
import io.realm.Realm
import okhttp3.Interceptor
import java.io.File

class AppApplication : BaseApplication() {
    companion object {
        var _context: BaseApplication? = null
        fun instance(): BaseApplication {
            return _context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        _context = this
        //realm
        Realm.init(this)
        val cacheDir = File(cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)

        if (com.rt.base.BuildConfig.is_debug) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this@AppApplication)
        //初始化数据库
        //支付宝沙箱环境
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        //初始化数据库
//        val mAppDatabase = Room.databaseBuilder(
//            applicationContext,
//            AppRoomDatabase::class.java, "android_room_xdj.db"
//        )
//            .allowMainThreadQueries()
//            .addMigrations(MIGRATION_2_3)
//            .build()
    }

//    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//        }
//    }

    override fun onAddOkHttpInterceptor(): List<Interceptor> {
        val list = ArrayList<Interceptor>()
        list.add(NewHeaderInterceptor())
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

}