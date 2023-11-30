package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.ParkingLotResultBean
import com.peakinfo.base.bean.ParkingSpaceBean
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.QRPayBean

class ParkingRepository : BaseRepository() {

    /**
     * 停车场泊位列表
     */
    suspend fun getParkingLotList(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingLotResultBean> {
        return mServer.getParkingLotList(param)
    }

    /**
     * 场内停车费查询
     */
    suspend fun parkingSpaceFee(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<ParkingSpaceBean> {
        return mServer.parkingSpaceFee(param)
    }

    /**
     *  场内支付
     */
    suspend fun insidePay(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<QRPayBean> {
        return mServer.insidePay(param)
    }

    /**
     *  支付结果
     */
    suspend fun payResult(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<PayResultBean> {
        return mServer.payResult(param)
    }
}