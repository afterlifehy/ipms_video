package com.rt.base.bean.ca

data class TokenInfoBean(
    val token: String? = null,
    val loginDevice: String? = null,
    val expiredTime: Long
)
