package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.NotificationBean
import com.peakinfo.base.bean.TransactionResultBean
import com.peakinfo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRecordViewModel: BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val transactionInquiryByOrderLiveData = MutableLiveData<TransactionResultBean>()
    val notificationInquiryLiveData = MutableLiveData<NotificationBean>()

    fun transactionInquiryByOrder(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.transactionInquiryByOrder(param)
            }
            executeResponse(response, {
                transactionInquiryByOrderLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun notificationInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.notificationInquiry(param)
            }
            executeResponse(response, {
                notificationInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}