package com.rt.base.request

import com.rt.base.bean.*
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
    suspend fun S_VO2_05(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 欠费支付
     */
    @POST("S_VO2_06")
    suspend fun S_VO2_06(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QRPayBean>

    /**
     * 主动查询支付结果
     */
    @POST("S_VO2_07")
    suspend fun S_VO2_07(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 欠费催缴单查询
     */
    @POST("S_VO2_08")
    suspend fun S_VO2_08(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 催缴单催缴
     */
    @POST("S_VO2_09")
    suspend fun S_VO2_09(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 异常上报
     */
    @POST("S_VO2_10")
    suspend fun S_VO2_10(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 签退
     */
    @POST("S_VO2_11")
    suspend fun logout(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any>

    /**
     * 查询订单
     */
    @POST("S_VO2_12")
    suspend fun S_VO2_12(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 交易查询
     */
    @POST("S_VO2_13")
    suspend fun S_VO2_13(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 根据订单查询交易记录
     */
    @POST("S_VO2_14")
    suspend fun S_VO2_14(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 查询告知书
     */
    @POST("S_VO2_15")
    suspend fun S_VO2_15(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

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
    suspend fun S_VO2_18(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>

    /**
     * 版本更新查询
     */
    @POST("S_VO2_19")
    suspend fun S_VO2_19(@Body param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean>
}