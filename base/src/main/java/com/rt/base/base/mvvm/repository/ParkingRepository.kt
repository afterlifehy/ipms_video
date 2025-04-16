package com.rt.base.base.mvvm.repository

import com.blankj.utilcode.util.EncryptUtils
import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.HttpWrapper2
import com.rt.base.bean.Login2Bean
import com.rt.base.bean.NoticePrintResultBean
import com.rt.base.bean.ParkingLotResultBean
import com.rt.base.bean.ParkingSpaceBean
import com.rt.base.bean.PayResultBean
import com.rt.base.bean.QRPayBean
import com.rt.base.bean.ca.FeeInfoBean
import com.rt.base.bean.ca.LoginInfoBean
import com.rt.base.bean.ca.OwemoneyInfoBean
import com.rt.base.bean.ca.QRInfoBean
import com.rt.base.bean.ca.QueryPayBean
import com.rt.base.bean.ca.TokenInfoBean
import com.rt.base.util.Constant
import retrofit2.http.Body

class ParkingRepository : BaseRepository() {

    /**
     * 停车场泊位列表
     */
    suspend fun getParkingLotList(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingLotResultBean> {
        return rtServer.getParkingLotList(param)
    }

    /**
     * 场内停车费查询
     */
    suspend fun parkingSpaceFee(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingSpaceBean> {
        return rtServer.parkingSpaceFee(param)
    }

    /**
     *  场内支付
     */
    suspend fun insidePay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QRPayBean> {
        return rtServer.insidePay(param)
    }

    /**
     *  支付结果
     */
    suspend fun payResult(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
        return rtServer.payResult(param)
    }

    /**
     * 登录
     */
    suspend fun login2(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Login2Bean> {
        return rtServer.login2(param)
    }

    /**
     * 签退
     */
    suspend fun logout(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.logout(param)
    }

    /**
     * 签退
     */
    suspend fun queryNoticeByOrderNo(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<NoticePrintResultBean> {
        return rtServer.queryNoticeByOrderNo(param)
    }

    /**
     * 通知签到签退
     */
    suspend fun logInOutNotice(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.logInOutNotice(param)
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
        return mServer.caLogin(param, options)
    }

    /**
     * 查询道路未离场停车费用
     */
    suspend fun fee(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<FeeInfoBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.fee(param, options)
    }

    /**
     * 欠费查询
     */
    suspend fun owemoney(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<List<OwemoneyInfoBean>> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.owemoney(param, options)
    }

    /**
     * 场内支付
     */
    suspend fun payonspot(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<QRInfoBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.payonspot(param, options)
    }

    /**
     * 支付结果查询
     */
    suspend fun querypay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<QueryPayBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.querypay(param, options)
    }

    /**
     * 取票/开票二维码
     */
    suspend fun invoiceQrcode(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<QRInfoBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.invoiceQrcode(param, options)
    }

    /**
     * 支付二维码请求通知
     */
    suspend fun qrNotice(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.qrNotice(param)
    }

    /**
     * 支付结果通知
     */
    suspend fun payResultNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
        return rtServer.payResultNotice(param)
    }
}