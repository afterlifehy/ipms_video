package com.rt.ipms_video.mvvm.repository

import com.alibaba.fastjson.JSONObject
import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.IncomeCountingBean
import com.rt.base.bean.LoginBean

class IncomeCountingRepository : BaseRepository() {

    /**
     * 营收盘点
     */
    suspend fun incomeCounting(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean> {
        return mServer.incomeCounting(param)
    }
}