package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.OrderRepository
import com.peakinfo.base.bean.ca.FreeParkBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FreeParkRecordViewModel: BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val freeParkLiveData = MutableLiveData<FreeParkBean>()
    fun freePark(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.freePark(param)
            }
            executeResponse(response, {
                freeParkLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "freePark"))
            })
        }
    }
}