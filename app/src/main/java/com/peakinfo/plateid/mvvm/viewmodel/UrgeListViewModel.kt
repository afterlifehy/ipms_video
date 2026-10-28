package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.OrderRepository
import com.peakinfo.base.bean.ca.UrgeBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UrgeListViewModel: BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val urgepaylistLiveData = MutableLiveData<List<UrgeBean>>()

    fun urgepaylist(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgepaylist(param)
            }
            executeResponse(response, {
                urgepaylistLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "urgepaylist"))
            })
        }
    }
}