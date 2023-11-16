package com.rt.base.bean

data class LoginBean(
    val name: String,
    val phone: String,
    val result: List<Result>,
    val token: String
)

data class Result(
    val streetName: String,
    val streetNo: String
)