package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.plateid.mvvm.repository.LogoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogoutViewModel : BaseViewModel() {
    val mLogoutRepository by lazy {
        LogoutRepository()
    }

    val logoutLiveData = MutableLiveData<Any>()

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
}