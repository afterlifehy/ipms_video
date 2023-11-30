package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.ParkingSpaceBean
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.QRPayBean
import com.peakinfo.plateid.mvvm.repository.ParkingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParkingSpaceViewModel: BaseViewModel() {
    val mParkingRepository by lazy {
        ParkingRepository()
    }

    val parkingSpaceFeeLiveData = MutableLiveData<ParkingSpaceBean>()
    val insidePayLiveData = MutableLiveData<QRPayBean>()
    val payResultLiveData = MutableLiveData<PayResultBean>()

    fun parkingSpaceFee(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.parkingSpaceFee(param)
            }
            executeResponse(response, {
                parkingSpaceFeeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun insidePay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.insidePay(param)
            }
            executeResponse(response, {
                insidePayLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun payResult(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.payResult(param)
            }
            executeResponse(response, {
                payResultLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}