package com.rt.ipms_geo.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.DebtCollectionResultBean
import com.rt.base.base.mvvm.repository.OrderRepository
import com.rt.base.bean.ca.OwemoneyInfoBean
import com.rt.base.bean.ca.UrgeBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DebtCollectionViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val debtInquiryLiveData = MutableLiveData<DebtCollectionResultBean>()
    val urgepaylistLiveData = MutableLiveData<List<UrgeBean>>()
    val owemoneyLiveData = MutableLiveData<List<OwemoneyInfoBean>>()

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

    fun owemoney(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.owemoney(param)
            }
            executeResponse(response, {
                owemoneyLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "owemoney"))
            })
        }
    }
}