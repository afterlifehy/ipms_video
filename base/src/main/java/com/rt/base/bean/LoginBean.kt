package com.rt.base.bean

data class LoginBean(
    val name: String,
    val loginName: String,
    val phone: String,
    val result: List<Street>,
    val token: String
)