package com.peakinfo.base.bean.ca

data class FreeParkDetailBean(
    val arrivedTime: Long,
    val berthId: String,
    val businessId: String,
    val districtId: String,
    val leftTime: Long,
    val plateColor: Int,
    val plateId: String,
    val roadId: String,
    val roadName: String
)