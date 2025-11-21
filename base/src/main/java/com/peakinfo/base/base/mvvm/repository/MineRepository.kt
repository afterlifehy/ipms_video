package com.peakinfo.base.base.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.FeeRateResultBean
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.Login2Bean
import com.peakinfo.base.bean.UpdateBean
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part

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

    /**
     * 日志上传
     */
    suspend fun logFileUpload(@Part file: MultipartBody.Part): HttpWrapper<Any> {
        return mFileServer.logFileUpload(file)
    }
}