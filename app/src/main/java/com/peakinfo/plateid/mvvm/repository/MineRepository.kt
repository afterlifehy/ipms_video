package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.FeeRateResultBean
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.UpdateBean

class MineRepository : BaseRepository() {
    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return mServer.checkUpdate(param)
    }

    /**
     * 签退
     */
    suspend fun logout(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.logout(param)
    }

    /**
     * 费率
     */
    suspend fun feeRate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<FeeRateResultBean> {
        return mServer.feeRate(param)
    }
}