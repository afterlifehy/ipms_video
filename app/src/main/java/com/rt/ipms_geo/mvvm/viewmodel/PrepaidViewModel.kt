package com.rt.ipms_geo.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.PayQRBean
import com.rt.base.bean.PayResultBean
import com.rt.base.base.mvvm.repository.OrderRepository
import com.rt.base.bean.ca.QRInfoBean
import com.rt.base.bean.ca.QueryPayBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrepaidViewModel : BaseViewModel() {
    val mOrderRepository by lazy {
        OrderRepository()
    }

    val prePayFeeInquiryLiveData = MutableLiveData<PayQRBean>()
    val payResultInquiryLiveData = MutableLiveData<PayResultBean>()
    val prepayLiveData = MutableLiveData<QRInfoBean>()
    val querypayLiveData = MutableLiveData<QueryPayBean>()
    val qrNoticeLiveData = MutableLiveData<Any>()
    val payResultNoticeLiveData = MutableLiveData<PayResultBean>()
    val invoiceQrcodeLiveData = MutableLiveData<QRInfoBean>()

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

    fun prepay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mOrderRepository.prepay(param)
            }
            executeResponse(response, {
                prepayLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "prepay"))
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
}