package com.peakinfo.base.base.mvvm

import com.peakinfo.base.bean.ResResponse
import com.peakinfo.base.http.RetrofitUtils
import com.peakinfo.base.request.Api


open class BaseRepository {

    val mServer by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getServerUrl()
        )
    }

    suspend fun <T : Any> apiCall(call: suspend () -> ResResponse<T>): ResResponse<T> {
        return call.invoke()
    }

}