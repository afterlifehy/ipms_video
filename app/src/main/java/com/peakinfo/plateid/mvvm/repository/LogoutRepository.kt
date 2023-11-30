package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.HttpWrapper

class LogoutRepository : BaseRepository() {

    /**
     * 签退
     */
    suspend fun logout(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.logout(param)
    }
}