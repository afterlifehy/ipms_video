package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.OrderRepository
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.QRPayBean
import com.peakinfo.base.bean.ca.OweMoneyBean
import com.peakinfo.base.bean.ca.QRInfoBean
import com.peakinfo.base.bean.ca.QueryPayBean
import com.peakinfo.base.bean.ca.UrgeDetailBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CAUrgeDetailViewModel: BaseViewModel() {

    val mOrderRepository by lazy {
        OrderRepository()
    }

    val urgepaydetailLiveData = MutableLiveData<UrgeDetailBean>()
    val consumeUrgePayLiveData = MutableLiveData<QRPayBean>()
    val urgepayQrcodeLiveData = MutableLiveData<QRInfoBean>()
    val querypayLiveData = MutableLiveData<QueryPayBean>()
    val payResultNoticeLiveData = MutableLiveData<PayResultBean>()
    val payowemoneyLiveData = MutableLiveData<OweMoneyBean>()
    val invoiceQrcodeLiveData = MutableLiveData<QRInfoBean>()

    fun urgepaydetail(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgepaydetail(param)
            }
            executeResponse(response, {
                urgepaydetailLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "urgepaydetail"))
            })
        }
    }

    fun payowemoney(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.payowemoney(param)
            }
            executeResponse(response, {
                payowemoneyLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = "", code = response.code, api = "payowemoney"))
            })
        }
    }

    fun consumeUrgePay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.consumeUrgePay(param)
            }
            executeResponse(response, {
                consumeUrgePayLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "consumeUrgePay"))
            })
        }
    }

    fun urgepayQrcode(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgepayQrcode(param)
            }
            executeResponse(response, {
                urgepayQrcodeLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "urgepayQrcode"))
            })
        }
    }

    fun querypay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.querypay(param)
            }
            executeResponse(response, {
                querypayLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "querypay"))
            })
        }
    }

    fun payResultNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.payResultNotice(param)
            }
            executeResponse(response, {
                payResultNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "payResultNotice"))
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