package com.peakinfo.base.http.interceptor

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.blankj.utilcode.util.GsonUtils
import com.peakinfo.base.R
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.event.ReLoginEvent
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.util.ToastUtil
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
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
            if (content.contains("status")) {
                val contentData = GsonUtils.fromJson(content, HttpWrapper::class.java)
                if (contentData.status == 1010||contentData.status == 1012 || contentData.status == 1015) {
                    EventBus.getDefault().post(ReLoginEvent())
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
                    ToastUtil.showMiddleToast(i18n(R.string.tokenExpired))
                    runBlocking {
//                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.wallet_address,"")
//                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.ticketCode,"")
//                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.userId,"")
                    }
//                    ARouter.getInstance().build(ARouterMap.LOGIN_START).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .navigation()
                    ActivityCacheManager.instance().getCurrentActivity()?.finish()
                }
            }


        }
    }
}