package com.rt.ipms_video.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.base.mvvm.repository.LoginRepository
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