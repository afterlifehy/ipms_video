package com.peakinfo.plateid.mvvm.repository

import com.peakinfo.base.base.mvvm.BaseRepository
import com.peakinfo.base.bean.HttpWrapper
import com.peakinfo.base.bean.IncomeCountingBean

class IncomeCountingRepository : BaseRepository() {

    /**
     * 营收盘点
     */
    suspend fun incomeCounting(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean> {
        return mServer.incomeCounting(param)
    }
}