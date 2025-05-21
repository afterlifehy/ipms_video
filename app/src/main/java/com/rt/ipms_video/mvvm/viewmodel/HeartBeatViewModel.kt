package com.rt.ipms_video.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.base.mvvm.repository.HeartBeatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HeartBeatViewModel: BaseViewModel() {

    val mHeartBeatRepository by lazy {
        HeartBeatRepository()
    }

    val heartbeatLiveData = MutableLiveData<Any>()

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
}