package com.rt.base.base.mvvm

import com.rt.base.bean.ResResponse
import com.rt.base.request.Api


open class BaseRepository {

    val mServer by lazy {
        com.rt.base.http.RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getServerUrl()
        )
    }

    suspend fun <T : Any> apiCall(call: suspend () -> ResResponse<T>): ResResponse<T> {
        return call.invoke()
    }

}