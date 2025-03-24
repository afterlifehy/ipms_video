package com.peakinfo.base.bean

data class HttpWrapper2<out T>(val message: String, val code: Int, val data: T)