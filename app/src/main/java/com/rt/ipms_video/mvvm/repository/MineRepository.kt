package com.rt.ipms_video.mvvm.repository

import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.UpdateBean
import com.rt.base.bean.VideoPicBean

class MineRepository : BaseRepository() {
    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return mServer.checkUpdate(param)
    }

    /**
     * 签退
     */
    suspend fun logout(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<Any> {
        return mServer.logout(param)
    }
}