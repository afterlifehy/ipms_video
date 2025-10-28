package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.HeartBeatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HeartBeatViewModel: BaseViewModel() {

    val mHeartBeatRepository by lazy {
        HeartBeatRepository()
    }

    val heartbeatLiveData = MutableLiveData<Any>()
    val locationUploadLiveData = MutableLiveData<Any>()

    fun heartbeat(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mHeartBeatRepository.heartbeat(param)
            }
            executeResponse(response, {
                heartbeatLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "heartbeat"))
            })
        }
    }

    fun locationUpload(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mHeartBeatRepository.locationUpload(param)
            }
            executeResponse(response, {
                locationUploadLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "locationUpload"))
            })
        }
    }
}