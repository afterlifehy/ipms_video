package com.rt.ipms_geo.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.base.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CollectionManagementViewModel : BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val callSubmitLiveData = MutableLiveData<Any>()
    val urgepayLiveData = MutableLiveData<Any>()

    fun callSubmit(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.callSubmit(param)
            }
            executeResponse(response, {
                callSubmitLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun urgepay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgepay(param)
            }
            executeResponse(response, {
                urgepayLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "urgepay"))
            })
        }
    }
}