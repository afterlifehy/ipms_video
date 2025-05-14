package com.rt.g2.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.NotificationBean
import com.rt.base.bean.TransactionResultBean
import com.rt.base.base.mvvm.repository.OrderRepository
import com.rt.base.bean.DebtCollectionResultBean
import com.rt.base.bean.ca.QRInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRecordViewModel: BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val transactionInquiryByOrderLiveData = MutableLiveData<TransactionResultBean>()
    val notificationInquiryLiveData = MutableLiveData<NotificationBean>()
    val invoiceQrcodeLiveData = MutableLiveData<QRInfoBean>()
    val debtInquiryLiveData = MutableLiveData<DebtCollectionResultBean>()

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

    fun invoiceQrcode(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.invoiceQrcode(param)
            }
            executeResponse(response, {
                invoiceQrcodeLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "invoiceQrcode"))
            })
        }
    }
}