package com.rt.base.base.mvvm.repository

import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.FeeRateResultBean
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.Login2Bean
import com.rt.base.bean.UpdateBean

class MineRepository : BaseRepository() {
    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return rtServer.checkUpdate(param)
    }

    /**
     * 签退
     */
    suspend fun logout(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.logout(param)
    }

    /**
     * 费率
     */
    suspend fun feeRate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<FeeRateResultBean> {
        return rtServer.feeRate(param)
    }

    /**
     * 登录
     */
    suspend fun login2(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Login2Bean> {
        return rtServer.login2(param)
    }
}