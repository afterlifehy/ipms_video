package com.rt.ipms_geo.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.Login2Bean
import com.rt.base.base.mvvm.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreetChooseViewModel : BaseViewModel() {

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