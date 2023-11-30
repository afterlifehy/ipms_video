package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.QRPayBean
import com.peakinfo.plateid.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DebtOrderDetailViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val debtPayLiveData = MutableLiveData<QRPayBean>()
    val payResultLiveData = MutableLiveData<PayResultBean>()
    fun debtPay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.debtPay(param)
            }
            executeResponse(response, {
                debtPayLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun payResult(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.payResult(param)
            }
            executeResponse(response, {
                payResultLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}