package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.UpdateBean

class LoginRepository : BaseRepository() {

    /**
     * 登录
     */
    suspend fun login(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean> {
        return mServer.login(param)
    }

    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return mServer.checkUpdate(param)
    }

}