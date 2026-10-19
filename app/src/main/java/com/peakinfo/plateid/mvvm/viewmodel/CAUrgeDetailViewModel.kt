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

class CAUrgeDetailViewModel : BaseViewModel() {

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
    val urgepayprintLiveData = MutableLiveData<Any>()
    val urgeBeanNoticeLiveData = MutableLiveData<Any>()
    val urgeDetailNoticeLiveData = MutableLiveData<Any>()
    val urgePayNoticeLiveData = MutableLiveData<Any>()
    val urgePayResultNoticeLiveData = MutableLiveData<Any>()
    val urgePrintNoticeLiveData = MutableLiveData<Any>()

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

    fun urgepayprint(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgepayprint(param)
            }
            executeResponse(response, {
                urgepayprintLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "urgepayprint"))
            })
        }
    }

    fun urgeBeanNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgeBeanNotice(param)
            }
            executeResponse(response, {
                urgeBeanNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "urgeBeanNotice"))
            })
        }
    }

    fun urgeDetailNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgeDetailNotice(param)
            }
            executeResponse(response, {
                urgeDetailNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "urgeDetailNotice"))
            })
        }
    }

    fun urgePayNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgePayNotice(param)
            }
            executeResponse(response, {
                urgePayNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "urgePayNotice"))
            })
        }
    }

    fun urgePayResultNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgePayResultNotice(param)
            }
            executeResponse(response, {
                urgePayResultNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "urgePayResultNotice"))
            })
        }
    }

    fun urgePrintNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.urgePrintNotice(param)
            }
            executeResponse(response, {
                urgePrintNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "urgePrintNotice"))
            })
        }
    }

}