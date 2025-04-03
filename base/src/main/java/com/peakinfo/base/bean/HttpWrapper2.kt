package com.peakinfo.base.bean

data class HttpWrapper2<out T>(
    val code: Int,
    val message: String,
    val data: T
)