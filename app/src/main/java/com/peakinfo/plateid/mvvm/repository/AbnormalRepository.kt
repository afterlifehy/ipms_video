package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.AbnormalReportResultBean
import com.peakinfo.base.bean.HttpWrapper

class AbnormalRepository : BaseRepository() {
    /**
     * 异常上报
     */
    suspend fun abnormalReport(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<AbnormalReportResultBean> {
        return mServer.abnormalReport(param)
    }
}