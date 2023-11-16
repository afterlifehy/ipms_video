package com.rt.base.bean

data class HttpWrapper<out T>(val status: Int, val msg: String, val attr: T)