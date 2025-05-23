package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.PayResultBean
import com.peakinfo.base.bean.QRPayBean
import com.peakinfo.base.base.mvvm.repository.OrderRepository
import com.peakinfo.base.bean.DebtCollectionResultBean
import com.peakinfo.base.bean.ca.OweMoneyBean
import com.peakinfo.base.bean.ca.QRInfoBean
import com.peakinfo.base.bean.ca.QueryPayBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DebtOrderDetailViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val debtPayLiveData = MutableLiveData<QRPayBean>()
    val payResultLiveData = MutableLiveData<PayResultBean>()
    val payowemoneyLiveData = MutableLiveData<OweMoneyBean>()
    val consumeonlineLiveData = MutableLiveData<QRInfoBean>()
    val querypayLiveData = MutableLiveData<QueryPayBean>()
    val qrNoticeLiveData = MutableLiveData<Any>()
    val payResultNoticeLiveData = MutableLiveData<PayResultBean>()
    val invoiceQrcodeLiveData = MutableLiveData<QRInfoBean>()
    val debtInquiryLiveData = MutableLiveData<DebtCollectionResultBean>()

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
                traverseErrorMsg(ErrorMessage(msg = "", code = response.status, api = "payResult"))
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

    fun consumeonline(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.consumeonline(param)
            }
            executeResponse(response, {
                consumeonlineLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = "", code = response.code, api = "consumeonline"))
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

    fun qrNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.qrNotice(param)
            }
            executeResponse(response, {
                qrNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "qrNotice"))
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
}