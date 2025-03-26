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
    val loginLiveData = MutableLiveData<LoginBean>()
    val caLoginLiveData = MutableLiveData<LoginInfoBean>()
    val checkUpdateLiveDate = MutableLiveData<UpdateBean>()
    val verifyAccountLiveDate = MutableLiveData<LoginBean>()
    val queryPwStatusLiveData = MutableLiveData<QueryPwStatusBean>()
    val tokenLiveData = MutableLiveData<TokenInfoBean>()
    val logoutLiveData = MutableLiveData<Any>()
    val logInOutNoticeLiveData = MutableLiveData<Any>()

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

    fun login(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.login(param)
            }
            executeResponse(response, {
                loginLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
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

    fun token(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.token(param)
            }
            executeResponse(response, {
                tokenLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "getToken"))
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
}