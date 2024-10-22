package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.plateid.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetPwViewModel : BaseViewModel() {
    val mLoginRepository by lazy {
        LoginRepository()
    }

    val editPwLiveData = MutableLiveData<Any>()
    fun editPw(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mLoginRepository.editPw(param)
            }
            executeResponse(response, {
                editPwLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}