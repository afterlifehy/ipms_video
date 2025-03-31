package com.peakinfo.base.base.mvvm.repository

import com.blankj.utilcode.util.EncryptUtils
import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.HttpWrapper2
import com.peakinfo.base.bean.Login2Bean
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.QueryPwStatusBean
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.bean.ca.LoginInfoBean
import com.peakinfo.base.bean.ca.QuerySimBean
import com.peakinfo.base.bean.ca.TokenInfoBean
import com.peakinfo.base.util.Constant
import retrofit2.http.Body
import retrofit2.http.POST

class LoginRepository : BaseRepository() {

    /**
     * 登录前查询
     */
    suspend fun querySim(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QuerySimBean> {
        return rtServer.querySim(param)
    }

    /**
     * 登录
     */
    suspend fun login(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean> {
        return rtServer.login(param)
    }

    /**
     * 登录
     */
    suspend fun caLogin(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<LoginInfoBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.login(param, options)
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
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return rtServer.checkUpdate(param)
    }

    /**
     * 登录
     */
    suspend fun login2(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Login2Bean> {
        return rtServer.login2(param)
    }

    /**
     * 登录密码验证
     */
    suspend fun verifyAccount(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean> {
        return rtServer.verifyAccount(param)
    }

    /**
     * 修改密码
     */
    suspend fun editPw(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.editPw(param)
    }

    /**
     * 查询是为初始密码
     */
    suspend fun queryPwStatus(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QueryPwStatusBean> {
        return rtServer.queryPwStatus(param)
    }

    /**
     * 通知更新证书
     */
    suspend fun reportUpdateCA(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return caReportServer.reportUpdateCA(param)
    }

    /**
     * 通知签到签退
     */
    suspend fun logInOutNotice(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.logInOutNotice(param)
    }
}