package com.rt.base.base.mvvm.repository

import com.blankj.utilcode.util.EncryptUtils
import com.rt.base.base.mvvm.BaseRepository
import com.rt.base.bean.HttpWrapper2
import com.rt.base.util.Constant

class HeartBeatRepository: BaseRepository() {

    /**
     * 收费设备心跳
     */
    suspend fun heartbeat(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper2<Any> {
        val nonce = EncryptUtils.encryptMD5ToString(Math.random().toString()).lowercase()
        val curTime = (System.currentTimeMillis() / 1000).toString()
        val checkSum = EncryptUtils.encryptSHA1ToString(Constant.PASSWORD + nonce + curTime).lowercase()
        val options: Map<String, String> = mapOf(
            "nonce" to nonce,
            "curTime" to curTime,
            "checkSum" to checkSum,
            "appId" to Constant.APP_ID
        )
        return mServer.heartbeat(param, options)
    }
}