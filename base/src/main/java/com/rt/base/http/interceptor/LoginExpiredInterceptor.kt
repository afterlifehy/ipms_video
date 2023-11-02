package com.rt.base.http.interceptor

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.GsonUtils
import com.rt.base.BaseApplication
import com.rt.base.R
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.LoginExpiredCheckData
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18n
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.ToastUtil
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

class LoginExpiredInterceptor : Interceptor {
    private var mHandler: LoginExpiredHandler


    init {
        mHandler = LoginExpiredHandler()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 200) {
            val mediaType = response.body!!.contentType()
            val content = response.body!!.string()
            if (content.contains("code")) {
                val mLoginExpiredCheckData = GsonUtils.fromJson(content, LoginExpiredCheckData::class.java)
                if (mLoginExpiredCheckData.code == 503) {//登录失效
                    val mMessage = mHandler.obtainMessage()
                    mMessage.what = 503
                    mMessage.obj = mLoginExpiredCheckData
                    mHandler.sendMessage(mMessage)
                }
                return response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build()

            } else {
                return response
            }
        } else {
            return response
        }
    }

    private class LoginExpiredHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                503 -> {//登录失效
                    ToastUtil.showToast(i18n(R.string.tokenExpired))
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.wallet_address,"")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.ticketCode,"")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.userId,"")
                    }
                    //Todo 登录失效跳转
//                    ARouter.getInstance().build(ARouterMap.LOGIN_START).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .navigation()
                    ActivityCacheManager.instance().getCurrentActivity()?.finish()
                }
            }


        }
    }
}