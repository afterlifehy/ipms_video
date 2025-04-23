package com.rt.base.base.mvvm.repository

import com.blankj.utilcode.util.EncryptUtils
import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.HttpWrapper2
import com.rt.base.bean.ca.TokenInfoBean
import com.rt.base.util.Constant

class LogoutRepository : BaseRepository() {

    /**
     * 签退
     */
    suspend fun logout(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.logout(param)
    }

    /**
     * 签退前获取token
     */
    suspend fun token(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<TokenInfoBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.token(param, options)
    }

    /**
     * 签退
     */
    suspend fun caLogout(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<Any> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.caLogout(param, options)
    }

    /**
     * 通知签到签退
     */
    suspend fun logInOutNotice(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.logInOutNotice(param)
    }
}