package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.LogoutRepository
import com.peakinfo.base.bean.ca.TokenInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogoutViewModel : BaseViewModel() {
    val mLogoutRepository by lazy {
        LogoutRepository()
    }

    val logoutLiveData = MutableLiveData<Any>()
    val tokenLiveData = MutableLiveData<TokenInfoBean>()
    val caLogoutLiveData = MutableLiveData<Any>()
    val logInOutNoticeLiveData = MutableLiveData<Any>()

    fun logout(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLogoutRepository.logout(param)
            }
            executeResponse(response, {
                logoutLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun token(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLogoutRepository.token(param)
            }
            executeResponse(response, {
                tokenLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "token"))
            })
        }
    }

    fun caLogout(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLogoutRepository.caLogout(param)
            }
            executeResponse(response, {
                logoutLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "caLogout"))
            })
        }
    }

    fun logInOutNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLogoutRepository.logInOutNotice(param)
            }
            executeResponse(response, {
                logInOutNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "notifyUpdateCert"))
            })
        }
    }
}