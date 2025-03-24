package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.PayQRBean
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.base.mvvm.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrepaidViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val prePayFeeInquiryLiveData = MutableLiveData<PayQRBean>()
    val payResultInquiryLiveData = MutableLiveData<PayResultBean>()

    fun prePayFeeInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.prePayFeeInquiry(param)
            }
            executeResponse(response, {
                prePayFeeInquiryLiveData.value = response.attr
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
                payResultInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = "", code = response.status))
            })
        }
    }
}