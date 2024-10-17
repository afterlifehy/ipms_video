package com.peakinfo.plateid.mvvm.viewmodel

import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.plateid.mvvm.repository.LoginRepository

class ResetPwViewModel : BaseViewModel() {
    class ResetPwViewModel: BaseViewModel() {
        val mLoginRepository by lazy {
            LoginRepository()
        }

//    val checkOnWorkLiveData = MutableLiveData<Any>()
//
//    fun checkOnWork(param: Map<String, Any?>) {
//        launch {
//            val response = withContext(Dispatchers.IO) {
//                mLoginRepository.checkOnWork(param)
//            }
//            executeResponse(response, {
//                checkOnWorkLiveData.value = response.attr
//            }, {
//                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
//            })
//        }
//    }
    }
}