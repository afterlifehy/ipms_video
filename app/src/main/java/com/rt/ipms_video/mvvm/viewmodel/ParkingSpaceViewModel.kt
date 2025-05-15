package com.rt.ipms_video.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.NoticePrintResultBean
import com.rt.base.bean.ParkingSpaceBean
import com.rt.base.bean.PayResultBean
import com.rt.base.bean.QRPayBean
import com.rt.base.base.mvvm.repository.ParkingRepository
import com.rt.base.bean.ca.FeeInfoBean
import com.rt.base.bean.ca.OwemoneyInfoBean
import com.rt.base.bean.ca.QRInfoBean
import com.rt.base.bean.ca.QueryPayBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParkingSpaceViewModel: BaseViewModel() {
    val mParkingRepository by lazy {
        ParkingRepository()
    }

    val parkingSpaceFeeLiveData = MutableLiveData<ParkingSpaceBean>()
    val insidePayLiveData = MutableLiveData<QRPayBean>()
    val payResultLiveData = MutableLiveData<PayResultBean>()
    val queryNoticeByOrderNoLiveData = MutableLiveData<NoticePrintResultBean>()
    val feeLiveData = MutableLiveData<FeeInfoBean>()
    val owemoneyLiveData = MutableLiveData<List<OwemoneyInfoBean>>()
    val payonspotLiveData = MutableLiveData<QRInfoBean>()
    val querypayLiveData = MutableLiveData<QueryPayBean>()
    val invoiceQrcodeLiveData = MutableLiveData<QRInfoBean>()
    val qrNoticeLiveData = MutableLiveData<Any>()
    val payResultNoticeLiveData = MutableLiveData<PayResultBean>()

    fun parkingSpaceFee(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.parkingSpaceFee(param)
            }
            executeResponse(response, {
                parkingSpaceFeeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun insidePay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.insidePay(param)
            }
            executeResponse(response, {
                insidePayLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun payResult(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.payResult(param)
            }
            executeResponse(response, {
                payResultLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = "", code = response.status))
            })
        }
    }

    fun queryNoticeByOrderNo(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.queryNoticeByOrderNo(param)
            }
            executeResponse(response, {
                queryNoticeByOrderNoLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun fee(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.fee(param)
            }
            executeResponse(response, {
                feeLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "fee"))
            })
        }
    }

    fun owemoney(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.owemoney(param)
            }
            executeResponse(response, {
                owemoneyLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "owemoney"))
            })
        }
    }

    fun payonspot(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.payonspot(param)
            }
            executeResponse(response, {
                payonspotLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "payonspot"))
            })
        }
    }

    fun querypay(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.querypay(param)
            }
            executeResponse(response, {
                querypayLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "querypay"))
            })
        }
    }

    fun invoiceQrcode(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.invoiceQrcode(param)
            }
            executeResponse(response, {
                invoiceQrcodeLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "invoiceQrcode"))
            })
        }
    }

    fun qrNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.qrNotice(param)
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
                mParkingRepository.payResultNotice(param)
            }
            executeResponse(response, {
                payResultNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "payResultNotice"))
            })
        }
    }
}