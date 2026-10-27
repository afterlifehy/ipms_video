package com.peakinfo.base.bean.ca

data class FreeParkBean(
    val date: Long,
    val freeCount: Int,
    val parkRecord: List<ParkRecordBean>,
    val plateColor: Int,
    val plateId: String,
    val remain: Int
)

data class ParkRecordBean(
    val arrivedTime: Long,
    val berthId: String,
    val businessId: String,
    val districtId: String,
    val dueMoney: Int,
    val leftTime: Long,
    val parkingTime: Int,
    val roadId: String,
    val roadName: String,
    val roadType: Int
)