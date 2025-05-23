package com.peakinfo.base.bean.ca

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UrgeBean(
    val dataTime: Long,
    val districtId: String,
    val plateColor: Int,
    val plateId: String,
    val urgePayFile: String,
    val urgePayId: String
): Parcelable