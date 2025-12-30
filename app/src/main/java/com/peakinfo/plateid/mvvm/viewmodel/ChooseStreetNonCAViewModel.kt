package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.base.mvvm.repository.LoginRepository
import com.peakinfo.base.bean.Login2Bean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChooseStreetNonCAViewModel: BaseViewModel() {

    val mLoginRepository by lazy {
        LoginRepository()
    }

    val login2LiveData = MutableLiveData<Login2Bean>()

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
}