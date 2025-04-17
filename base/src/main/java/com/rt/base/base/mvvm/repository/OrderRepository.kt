package com.rt.base.base.mvvm.repository

import com.blankj.utilcode.util.EncryptUtils
import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.DebtCollectionResultBean
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.HttpWrapper2
import com.rt.base.bean.NotificationBean
import com.rt.base.bean.OrderResultBean
import com.rt.base.bean.PayQRBean
import com.rt.base.bean.PayResultBean
import com.rt.base.bean.QRPayBean
import com.rt.base.bean.TransactionResultBean
import com.rt.base.bean.VideoPicBean
import com.rt.base.bean.ca.OweMoneyBean
import com.rt.base.bean.ca.OwemoneyInfoBean
import com.rt.base.bean.ca.QRInfoBean
import com.rt.base.bean.ca.QueryPayBean
import com.rt.base.bean.ca.UrgeBean
import com.rt.base.util.Constant
import retrofit2.http.Body

class OrderRepository : BaseRepository() {

    /**
     * 视频图片
     */
    suspend fun videoPic(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<VideoPicBean> {
        return rtServer.videoPic(param)
    }

    /**
     * 欠费查询
     */
    suspend fun debtInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtCollectionResultBean> {
        return rtServer.debtInquiry(param)
    }

    /**
     * 欠费支付
     */
    suspend fun debtPay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QRPayBean> {
        return rtServer.debtPay(param)
    }

    /**
     * 查询支付结果
     */
    suspend fun payResult(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
        return rtServer.payResult(param)
    }

    /**
     * 订单查询
     */
    suspend fun orderInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<OrderResultBean> {
        return rtServer.orderInquiry(param)
    }

    /**
     * 根据order交易查询
     */
    suspend fun transactionInquiryByOrder(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean> {
        return rtServer.transactionInquiryByOrder(param)
    }

    /**
     * 告知书查询
     */
    suspend fun notificationInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<NotificationBean> {
        return rtServer.notificationInquiry(param)
    }

    /**
     * 交易查询
     */
    suspend fun transactionInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean> {
        return rtServer.transactionInquiry(param)
    }

    /**
     * 催缴单催缴
     */
    suspend fun callSubmit(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.callSubmit(param)
    }

    /**
     * 欠费催缴单查询
     */
    suspend fun callInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return rtServer.callInquiry(param)
    }

    /**
     * 预支付查询
     */
    suspend fun prePayFeeInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean> {
        return rtServer.prePayFeeInquiry(param)
    }

    /**
     * 预付费停车费
     */
    suspend fun prepay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<QRInfoBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.prepay(param, options)
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

    /**
     * 欠费支付请求
     */
    suspend fun payowemoney(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<OweMoneyBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.payowemoney(param, options)
    }

    /**
     * 平台支付二维码
     */
    suspend fun consumeonline(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<QRInfoBean> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.consumeonline(param, options)
    }

    /**
     * 欠费催缴单查询
     */
    suspend fun urgepaylist(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<List<UrgeBean>> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.urgepaylist(param, options)
    }

    /**
     * 欠费催缴单查询
     */
    suspend fun urgepay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<Any> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.urgepay(param, options)
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
}