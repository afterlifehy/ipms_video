package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.DebtCollectionResultBean
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.NotificationBean
import com.peakinfo.base.bean.OrderResultBean
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.QRPayBean
import com.peakinfo.base.bean.TransactionResultBean
import com.peakinfo.base.bean.VideoPicBean

class OrderRepository : BaseRepository() {

    /**
     * 视频图片
     */
    suspend fun videoPic(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<VideoPicBean> {
        return mServer.videoPic(param)
    }

    /**
     * 欠费查询
     */
    suspend fun debtInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtCollectionResultBean> {
        return mServer.debtInquiry(param)
    }

    /**
     * 欠费支付
     */
    suspend fun debtPay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QRPayBean> {
        return mServer.debtPay(param)
    }

    /**
     * 查询支付结果
     */
    suspend fun payResult(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
        return mServer.payResult(param)
    }

    /**
     * 订单查询
     */
    suspend fun orderInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<OrderResultBean> {
        return mServer.orderInquiry(param)
    }

    /**
     * 根据order交易查询
     */
    suspend fun transactionInquiryByOrder(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean> {
        return mServer.transactionInquiryByOrder(param)
    }

    /**
     * 告知书查询
     */
    suspend fun notificationInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<NotificationBean> {
        return mServer.notificationInquiry(param)
    }

    /**
     * 交易查询
     */
    suspend fun transactionInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean> {
        return mServer.transactionInquiry(param)
    }

    /**
     * 催缴单催缴
     */
    suspend fun callSubmit(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.callSubmit(param)
    }

    /**
     * 欠费催缴单查询
     */
    suspend fun callInquiry(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.callInquiry(param)
    }
}