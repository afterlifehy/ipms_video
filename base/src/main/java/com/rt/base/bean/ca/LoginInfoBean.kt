package com.rt.base.bean.ca

import java.io.Serializable

data class LoginInfoBean(val serverTime: Long, val token: String) : Serializable
