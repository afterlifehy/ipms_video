package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.Login2Bean
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.QueryPwStatusBean
import com.peakinfo.base.bean.UpdateBean
import retrofit2.http.Body
import retrofit2.http.POST

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

    /**
     * 登录
     */
    suspend fun login2(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Login2Bean> {
        return mServer.login2(param)
    }

    /**
     * 登录密码验证
     */
    suspend fun verifyAccount(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean> {
        return mServer.verifyAccount(param)
    }

    /**
     * 修改密码
     */
    suspend fun editPw(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.editPw(param)
    }

    /**
     * 查询是为初始密码
     */
    suspend fun queryPwStatus(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QueryPwStatusBean> {
        return mServer.queryPwStatus(param)
    }
}