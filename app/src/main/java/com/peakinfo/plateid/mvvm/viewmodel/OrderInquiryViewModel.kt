package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.OrderResultBean
import com.peakinfo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderInquiryViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val orderInquiryLiveData = MutableLiveData<OrderResultBean>()

    fun orderInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.orderInquiry(param)
            }
            executeResponse(response, {
                orderInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}