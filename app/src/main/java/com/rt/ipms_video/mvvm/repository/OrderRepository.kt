package com.rt.ipms_video.mvvm.repository

import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.LoginBean
import com.rt.base.bean.VideoPicBean

class OrderRepository : BaseRepository() {

    /**
     * 视频图片
     */
    suspend fun videoPic(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<VideoPicBean> {
        return mServer.videoPic(param)
    }
}