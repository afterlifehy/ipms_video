package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.QueryPwStatusBean
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.base.mvvm.repository.LoginRepository
import com.peakinfo.base.bean.ca.LoginInfoBean
import com.peakinfo.base.bean.ca.QuerySimBean
import com.peakinfo.base.bean.ca.TokenInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : BaseViewModel() {

    val mLoginRepository by lazy {
        LoginRepository()
    }

    val querySimLiveData = MutableLiveData<QuerySimBean>()
    val checkUpdateLiveDate = MutableLiveData<UpdateBean>()
    val verifyAccountLiveDate = MutableLiveData<LoginBean>()
    val caVerifyAccountLiveData = MutableLiveData<Any>()
    val queryPwStatusLiveData = MutableLiveData<QueryPwStatusBean>()
    val tokenLiveData = MutableLiveData<TokenInfoBean>()
    val logoutLiveData = MutableLiveData<Any>()
    val notifyUpdateCertLiveData = MutableLiveData<Any>()
    val refreshCertLiveData = MutableLiveData<Any>()

    fun notifyUpdateCert(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.notifyUpdateCert(param)
            }
            executeResponse(response, {
                notifyUpdateCertLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "notifyUpdateCert"))
            })
        }
    }

    fun querySim(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.querySim(param)
            }
            executeResponse(response, {
                querySimLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "querySim"))
            })
        }
    }

    fun token(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.token(param)
            }
            executeResponse(response, {
                tokenLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "token"))
            })
        }
    }

    fun logout(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.caLogout(param)
            }
            executeResponse(response, {
                logoutLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "caLogout"))
            })
        }
    }

    fun checkUpdate(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.checkUpdate(param)
            }
            executeResponse(response, {
                checkUpdateLiveDate.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun verifyAccount(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.verifyAccount(param)
            }
            executeResponse(response, {
                verifyAccountLiveDate.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun caVerifyAccount(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.caVerifyAccount(param)
            }
            executeResponse(response, {
                caVerifyAccountLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "caVerifyAccount"))
            })
        }
    }

    fun queryPwStatus(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.queryPwStatus(param)
            }
            executeResponse(response, {
                queryPwStatusLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "queryPwStatus"))
            })
        }
    }

    fun refreshCert(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.refreshCert(param)
            }
            executeResponse(response, {
                refreshCertLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "refreshCert"))
            })
        }
    }

}