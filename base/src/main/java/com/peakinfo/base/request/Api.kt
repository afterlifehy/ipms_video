package com.peakinfo.base.request

import com.peakinfo.base.bean.*
import com.peakinfo.base.bean.ca.DebtBean
import com.peakinfo.base.bean.ca.FeeInfoBean
import com.peakinfo.base.bean.ca.LoginInfoBean
import com.peakinfo.base.bean.ca.OweMoneyBean
import com.peakinfo.base.bean.ca.OwemoneyInfoBean
import com.peakinfo.base.bean.ca.QRInfoBean
import com.peakinfo.base.bean.ca.QueryPayBean
import com.peakinfo.base.bean.ca.QuerySimBean
import com.peakinfo.base.bean.ca.TokenInfoBean
import com.peakinfo.base.bean.ca.UrgeBean
import okhttp3.MultipartBody
import retrofit2.http.*


interface Api {
    /**
     * 签到
     */
    @POST("S_VO2_01")
    suspend fun login(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 停车场泊位列表
     */
    @POST("S_VO2_02")
    suspend fun getParkingLotList(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingLotResultBean>

    /**
     * 场内停车费查询
     */
    @POST("S_VO2_03")
    suspend fun parkingSpaceFee(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingSpaceBean>

    /**
     * 场内支付
     */
    @POST("S_VO2_04")
    suspend fun insidePay(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QRPayBean>

    /**
     * 欠费查询
     */
    @POST("S_VO2_05")
    suspend fun debtInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<DebtCollectionResultBean>

    /**
     * CA欠费查询
     */
    @POST("owemoney")
    suspend fun caDebtInquiry(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<List<DebtBean>>
    /**
     * 欠费支付
     */
    @POST("S_VO2_06")
    suspend fun debtPay(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QRPayBean>

    /**
     * 主动查询支付结果
     */
    @POST("S_VO2_07")
    suspend fun payResult(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean>

    /**
     * 欠费催缴单查询
     */
    @POST("S_VO2_08")
    suspend fun callInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 催缴单催缴
     */
    @POST("S_VO2_09")
    suspend fun callSubmit(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 异常上报
     */
    @POST("S_VO2_10")
    suspend fun abnormalReport(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<AbnormalReportResultBean>

    /**
     * 签退
     */
    @POST("S_VO2_11")
    suspend fun logout(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 查询订单
     */
    @POST("S_VO2_12")
    suspend fun orderInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<OrderResultBean>

    /**
     * 交易查询
     */
    @POST("S_VO2_13")
    suspend fun transactionInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean>

    /**
     * 根据订单查询交易记录
     */
    @POST("S_VO2_14")
    suspend fun transactionInquiryByOrder(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<TransactionResultBean>

    /**
     * 查询告知书
     */
    @POST("S_VO2_15")
    suspend fun notificationInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<NotificationBean>

    /**
     * 视频及图片地址查询
     */
    @POST("S_VO2_16")
    suspend fun videoPic(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<VideoPicBean>

    /**
     * 营收盘点查询
     */
    @POST("S_VO2_17")
    suspend fun incomeCounting(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean>

    /**
     * 费率查询
     */
    @POST("S_VO2_18")
    suspend fun feeRate(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<FeeRateResultBean>

    /**
     * 版本更新查询
     */
    @POST("S_V_01")
    suspend fun checkUpdate(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean>

    /**
     * 考勤排班
     */
    @POST("S_VO2_22")
    suspend fun login2(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Login2Bean>

    /**
     * 登录密码验证
     */
    @POST("S_VO2_21_0")
    suspend fun verifyAccount(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 验证密码
     */
    @POST("S_V_04")
    suspend fun caVerifyAccount(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 修改密码
     */
    @POST("S_V_03")
    suspend fun editPw(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 查询是为初始密码
     */
    @POST("S_V_05")
    suspend fun queryPwStatus(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QueryPwStatusBean>

    /**
     *预支付查询
     */
    @POST("S_VO2_23")
    suspend fun prePayFeeInquiry(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayQRBean>

    /**
     *多告知书打印
     */
    @POST("S_VO2_24")
    suspend fun queryNoticeByOrderNo(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<NoticePrintResultBean>

    /**
     * 登录
     */
    @POST("login")
    suspend fun caLogin(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<LoginInfoBean>

    /**
     * 签退前获取token
     */
    @POST("token")
    suspend fun token(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<TokenInfoBean>

    /**
     * 签退
     */
    @POST("logout")
    suspend fun caLogout(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<Any>

    /**
     * 欠费查询
     */
    @POST("owemoney")
    suspend fun owemoney(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<List<OwemoneyInfoBean>>

    /**
     * 查询道路未离场停车费用
     */
    @POST("fee")
    suspend fun fee(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<FeeInfoBean>

    /**
     * 场内支付
     */
    @POST("payonspot")
    suspend fun payonspot(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<QRInfoBean>

    /**
     * 欠费支付请求
     */
    @POST("payowemoney")
    suspend fun payowemoney(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<OweMoneyBean>

    /**
     * 平台支付二维码
     */
    @POST("consumeonline")
    suspend fun consumeonline(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<QRInfoBean>

    /**
     * 支付结果查询
     */
    @POST("querypay")
    suspend fun querypay(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<QueryPayBean>

    /**
     * 欠费催缴单查询
     */
    @POST("urgepaylist")
    suspend fun urgepaylist(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<List<UrgeBean>>

    /**
     * 欠费单催缴
     */
    @POST("urgepay")
    suspend fun urgepay(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<Any>

    /**
     * 预付费停车费
     */
    @POST("prepay")
    suspend fun prepay(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<QRInfoBean>

    /**
     * 取票/开票二维码
     */
    @POST("invoice/qrcode")
    suspend fun invoiceQrcode(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<QRInfoBean>

    /**
     * 通知壬通平台刷新证书
     */
    @POST("S_G1_01")
    suspend fun reportUpdateCA(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 登录前查询
     */
    @POST("S_VO2_25")
    suspend fun querySim(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QuerySimBean>

    /**
     * 通知签到签退
     */
    @POST("S_VO2_26")
    suspend fun logInOutNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 欠费催缴单查询
     */
    @POST("S_VO2_27")
    suspend fun qrNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 支付结果通知
     */
    @POST("S_VO2_28")
    suspend fun payResultNotice(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean>

    /**
     * 通知平台更新Cert
     */
    @POST("S_GECE_G1")
    suspend fun notifyUpdateCert(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 刷新Cert
     */
    @POST("config/cert/refresh")
    suspend fun refreshCert(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<Any>

    /**
     * 收费设备心跳
     */
    @POST("heartbeat")
    suspend fun heartbeat(
        @Body param: @JvmSuppressWildcards Map<String, Any?>, @QueryMap options: @JvmSuppressWildcards Map<String, String>
    ): HttpWrapper2<Any>

    /**
     * 轨迹上传
     */
    @POST("S_VO3_02")
    suspend fun locationUpload(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 考勤排班
     */
    @POST("S_VO2_20")
    suspend fun checkOnWork(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<Any>

    /**
     * 签退考勤排班
     */
    @POST("S_VO3_20")
    suspend fun logoutCheckOnWork(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<Any>

    /**
     * 日志上传
     */
    @Multipart
    @POST("upload")
    suspend fun logFileUpload(@Part file: MultipartBody.Part): HttpWrapper<Any>
}