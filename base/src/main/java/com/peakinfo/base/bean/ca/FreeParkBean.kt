package com.peakinfo.base.bean.ca

data class FreeParkBean(
    val date: Long,
    val freeCount: Int,
    val plateColor: Int,
    val plateId: String,
    val remain: Int
)