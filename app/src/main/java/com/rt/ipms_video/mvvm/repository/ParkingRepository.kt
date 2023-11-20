package com.rt.ipms_video.mvvm.repository

import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.ParkingLotResultBean
import com.rt.base.bean.ParkingSpaceBean
import com.rt.base.bean.QRPayBean
import retrofit2.http.Body

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
}