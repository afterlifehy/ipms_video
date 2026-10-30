package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.OrderRepository
import com.peakinfo.base.bean.ca.FreeParkBean
import com.peakinfo.base.bean.ca.FreeParkDetailBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FreeParkRecordViewModel: BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val freeParkDetailLiveData = MutableLiveData<List<FreeParkDetailBean>>()
    fun freeParkDetail(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.freeParkDetail(param)
            }
            executeResponse(response, {
                freeParkDetailLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "freeParkDetail"))
            })
        }
    }
}