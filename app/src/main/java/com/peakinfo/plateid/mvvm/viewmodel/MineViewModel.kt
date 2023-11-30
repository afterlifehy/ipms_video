package com.peakinfo.plateid.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.peakinfo.base.base.mvvm.BaseViewModel
import com.peakinfo.base.base.mvvm.ErrorMessage
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.plateid.mvvm.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MineViewModel : BaseViewModel() {

    val mMineRepository by lazy {
        MineRepository()
    }

    val checkUpdateLiveDate = MutableLiveData<UpdateBean>()
    val logoutLiveData = MutableLiveData<Any>()

    fun checkUpdate(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.checkUpdate(param)
            }
            executeResponse(response, {
                checkUpdateLiveDate.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }

    fun logout(param: Map<String, Any?>) {
        launch {
            val response = withContext(Dispatchers.IO) {
                mMineRepository.logout(param)
            }
            executeResponse(response, {
                logoutLiveData.value = response.attr
            }, {
                traverseErrorMsg(ErrorMessage(msg = response.msg, code = response.status))
            })
        }
    }
}