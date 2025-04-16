package com.rt.base.base.mvvm

import com.rt.base.bean.ResResponse
import com.rt.base.http.RetrofitUtils
import com.rt.base.request.Api


open class BaseRepository {

    val mServer by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getServerUrl()
        )
    }

    val rtServer by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getRTServerUrl()
        )
    }

    val caReportServer by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getCAReportUrl()
        )
    }

    suspend fun <T : Any> apiCall(call: suspend () -> ResResponse<T>): ResResponse<T> {
        return call.invoke()
    }

}