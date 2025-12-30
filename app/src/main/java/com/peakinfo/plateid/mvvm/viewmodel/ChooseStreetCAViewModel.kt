package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.LoginRepository
import com.peakinfo.base.bean.ca.LoginInfoBean
import com.peakinfo.base.bean.ca.TokenInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChooseStreetCAViewModel: BaseViewModel() {

    val mLoginRepository by lazy {
        LoginRepository()
    }

    val checkOnWorkLiveData = MutableLiveData<Any>()
    val caLoginLiveData = MutableLiveData<LoginInfoBean>()
    val logInOutNoticeLiveData = MutableLiveData<Any>()
    val tokenLiveData = MutableLiveData<TokenInfoBean>()
    val logoutLiveData = MutableLiveData<Any>()
    val refreshCertLiveData = MutableLiveData<Any>()

    fun checkOnWork(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.checkOnWork(param)
            }
            executeResponse(response, {
                checkOnWorkLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code))
            })
        }
    }

    fun caLogin(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.caLogin(param)
            }
            executeResponse(response, {
                caLoginLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "caLogin"))
            })
        }
    }

    fun logInOutNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.logInOutNotice(param)
            }
            executeResponse(response, {
                logInOutNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "notifyUpdateCert"))
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