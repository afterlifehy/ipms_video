package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.Login2Bean
import com.peakinfo.base.base.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreetChooseViewModel : BaseViewModel() {

    val mLoginRepository by lazy {
        LoginRepository()
    }

    val login2LiveData = MutableLiveData<Login2Bean>()
    val checkOnWorkLiveData = MutableLiveData<Any>()

    fun login2(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.login2(param)
            }
            executeResponse(response, {
                login2LiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

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
}