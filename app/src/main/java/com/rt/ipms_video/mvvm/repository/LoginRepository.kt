package com.rt.ipms_video.mvvm.repository

import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper
import com.rt.base.bean.LoginBean

class LoginRepository : BaseRepository() {

    /**
     * 登录
     */
    suspend fun login(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<LoginBean> {
        return mServer.login(param)
    }
}