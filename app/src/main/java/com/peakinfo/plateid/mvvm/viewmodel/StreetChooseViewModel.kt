package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.plateid.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreetChooseViewModel : BaseViewModel() {

    val mLoginRepository by lazy {
        LoginRepository()
    }

    val checkOnWorkLiveData = MutableLiveData<Any>()
    fun checkOnWork(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.checkOnWork(param)
            }
            executeResponse(response, {
                checkOnWorkLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}