package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.DebtCollectionResultBean
import com.peakinfo.base.bean.QRPayBean
import com.peakinfo.base.base.mvvm.repository.OrderRepository
import com.peakinfo.base.bean.ca.UrgepayBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DebtCollectionViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val debtInquiryLiveData = MutableLiveData<DebtCollectionResultBean>()
    val urgepaylistLiveData = MutableLiveData<List<UrgepayBean>>()

    fun debtInquiry(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.debtInquiry(param)
            }
            executeResponse(response, {
                debtInquiryLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

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