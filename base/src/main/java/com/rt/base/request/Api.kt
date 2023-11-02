package com.rt.base.request

import com.rt.base.bean.*
import retrofit2.http.*


interface Api {
    /**
     * 添加钱包地址
     */
    @POST("spendingAccount/addWalletAddress")
    suspend fun addWalletAddress(@Body param: Map<String, String>): HttpWrapper<Boolean>

}