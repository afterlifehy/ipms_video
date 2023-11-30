package com.peakinfo.base.viewbase.inter

import com.peakinfo.base.bean.NetWorkRequestData

interface NetWorkRequestLinsener {
    /**
     * 网络超时之类的错误
     */
    fun onNetWorkRequestError(errror: NetWorkRequestData)

    /**
     * 无网络错误
     */
    fun onNoNetWorkErrror(errror: NetWorkRequestData)
}