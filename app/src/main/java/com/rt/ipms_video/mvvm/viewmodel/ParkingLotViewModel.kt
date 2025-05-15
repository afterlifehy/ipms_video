package com.rt.ipms_video.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rt.base.base.mvvm.BaseViewModel
import com.rt.base.base.mvvm.ErrorMessage
import com.rt.base.bean.Login2Bean
import com.rt.base.bean.ParkingLotResultBean
import com.rt.base.base.mvvm.repository.ParkingRepository
import com.rt.base.bean.ca.LoginInfoBean
import com.rt.base.bean.ca.TokenInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParkingLotViewModel : BaseViewModel() {
    val mParkingRepository by lazy {
        ParkingRepository()
    }

    val parkingLotListLiveData = MutableLiveData<ParkingLotResultBean>()
    val login2LiveData = MutableLiveData<Login2Bean>()
    val logoutLiveData = MutableLiveData<Any>()
    val tokenLiveData = MutableLiveData<TokenInfoBean>()
    val logInOutNoticeLiveData = MutableLiveData<Any>()
    val caLoginLiveData = MutableLiveData<LoginInfoBean>()
    val caLogoutLiveData = MutableLiveData<Any>()

    fun getParkingLotList(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.getParkingLotList(param)
            }
            executeResponse(response, {
                parkingLotListLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun login2(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.login2(param)
            }
            executeResponse(response, {
                login2LiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "login2"))
            })
        }
    }

    fun logout(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.logout(param)
            }
            executeResponse(response, {
                logoutLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun caLogin(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.caLogin(param)
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
                mParkingRepository.token(param)
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
                mParkingRepository.caLogout(param)
            }
            executeResponse(response, {
                caLogoutLiveData.value = response.data
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.message, code = response.code, api = "caLogout"))
            })
        }
    }

    fun logInOutNotice(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mParkingRepository.logInOutNotice(param)
            }
            executeResponse(response, {
                logInOutNoticeLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status, api = "logInOutNotice"))
            })
        }
    }

}